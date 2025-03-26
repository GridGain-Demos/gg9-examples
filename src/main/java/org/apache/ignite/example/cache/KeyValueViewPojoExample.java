/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.cache;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.table.KeyValueView;
import org.apache.ignite.table.mapper.Mapper;
import org.gridgain.cache.store.jdbc.JdbcCacheStoreFactory;
import org.gridgain.cache.store.jdbc.JdbcType;
import org.gridgain.cache.store.jdbc.JdbcTypeField;
import org.gridgain.cache.store.jdbc.dialect.H2Dialect;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.RunScript;

/**
 * This example demonstrates the usage of the {@link KeyValueView} POJO API with read/write through to
 * JDBC Cache Store. It uses embedded H2 database as the cache store backend.
 *
 * <p>To run the example, do the following:
 * <ol>
 *     <li>Import the examples project into your IDE.</li>
 *     <li>
 *         Download and prepare artifacts for running an Ignite node using the CLI tool (if not done yet):<br>
 *         {@code ignite bootstrap}
 *     </li>
 *     <li>
 *         Start an Ignite node using the CLI tool:<br>
 *         {@code ignite node start --config=$IGNITE_HOME/examples/config/ignite-config.conf my-first-node}
 *     </li>
 *     <li>
 *         Cluster initialization using the CLI tool (if not done yet):<br>
 *         {@code ignite cluster init --name=ignite-cluster --node-endpoint=localhost:10300 --meta-storage-node=my-first-node}
 *     </li>
 *     <li>Run the example in the IDE.</li>
 *     <li>
 *         Stop the Ignite node using the CLI tool:<br>
 *         {@code ignite node stop my-first-node}
 *     </li>
 * </ol>
 */
public class KeyValueViewPojoExample {
    /**
     * Main method of the example.
     *
     * @param args The command line arguments.
     * @throws Exception If failed.
     */
    public static void main(String[] args) throws Exception {
        //--------------------------------------------------------------------------------------
        //
        // Creating 'accounts' table.
        //
        //--------------------------------------------------------------------------------------

        try (
                Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1:10800/");
                Statement stmt = conn.createStatement()
        ) {
            stmt.executeUpdate("CREATE ZONE CACHES WITH STORAGE_PROFILES='in-memory'");

            // Cache can be only created using in-memory storage.
            stmt.executeUpdate(
                    "CREATE CACHE accounts ("
                            + "accountNumber INT PRIMARY KEY,"
                            + "firstName     VARCHAR,"
                            + "lastName      VARCHAR,"
                            + "balance       DOUBLE) ZONE CACHES"
            );
        }

        //--------------------------------------------------------------------------------------
        //
        // Starting local H2 instance and creating corresponding cached table.
        //
        //--------------------------------------------------------------------------------------

        JdbcConnectionPool dataSrc = JdbcConnectionPool.create("jdbc:h2:mem:GridGainKeyValueViewPojoExample", "sa", "");
        String initScript = "CREATE TABLE accounts (\n"
                + "    accountNumber INT PRIMARY KEY,\n"
                + "    firstName           VARCHAR,\n"
                + "    lastName           VARCHAR,\n"
                + "    balance              DOUBLE);\n"
                + "INSERT INTO accounts VALUES (789, 'John', 'Smith', 200);";

        RunScript.execute(dataSrc.getConnection(), new StringReader(initScript));

        //--------------------------------------------------------------------------------------
        //
        // Creating a client to connect to the cluster.
        //
        //--------------------------------------------------------------------------------------

        System.out.println("\nConnecting to server...");

        try (IgniteClient client = IgniteClient.builder()
                .addresses("127.0.0.1:10800")
                .build()
        ) {
            //--------------------------------------------------------------------------------------
            //
            // Configuring cache store factory.
            //
            //--------------------------------------------------------------------------------------

            JdbcCacheStoreFactory storeFactory = new JdbcCacheStoreFactory();
            storeFactory.setDialect(new H2Dialect());

            JdbcType jdbcType = new JdbcType();

            jdbcType.setDatabaseSchema("PUBLIC");
            jdbcType.setDatabaseTable("accounts");

            jdbcType.setKeyType(AccountKey.class);
            jdbcType.setKeyFields(new JdbcTypeField(Types.INTEGER, "accountNumber", Integer.class, "accountNumber"));

            jdbcType.setValueType(Account.class);
            jdbcType.setValueFields(
                    new JdbcTypeField(Types.VARCHAR, "firstName", String.class, "firstName"),
                    new JdbcTypeField(Types.VARCHAR, "lastName", String.class, "lastName"),
                    new JdbcTypeField(Types.DOUBLE, "balance", Double.class, "balance")
            );

            storeFactory.setType(jdbcType);
            storeFactory.setDataSource(dataSrc);

            //--------------------------------------------------------------------------------------
            //
            // Creating a key-value view for the 'accounts' table.
            //
            //--------------------------------------------------------------------------------------

            KeyValueView<AccountKey, Account> kvView = client.caches().cache("accounts")
                    .keyValueView(storeFactory, Mapper.of(AccountKey.class), Mapper.of(Account.class));

            //--------------------------------------------------------------------------------------
            //
            // Performing the 'put' operation.
            //
            //--------------------------------------------------------------------------------------

            System.out.println("\nInserting a key-value pair into the 'accounts' table...");

            AccountKey key = new AccountKey(123456);

            Account value = new Account(
                    "Val",
                    "Kulichenko",
                    100.00d
            );

            kvView.put(null, key, value); // Will be propagated to cache store.

            //--------------------------------------------------------------------------------------
            //
            // Performing the 'get' operation.
            //
            //--------------------------------------------------------------------------------------

            System.out.println("\nRetrieving a value using KeyValueView API...");

            value = kvView.get(null, key);

            System.out.println(
                    "\nRetrieved value:\n"
                            + "    Account Number: " + key.accountNumber + '\n'
                            + "    Owner: " + value.firstName + " " + value.lastName + '\n'
                            + "    Balance: $" + value.balance);

            System.out.println("\nChecking write-through...");

            ResultSet rs = RunScript.execute(dataSrc.getConnection(),
                    new StringReader("SELECT * from accounts where accountNumber=123456;"));

            while (rs.next()) {
                System.out.println("    "
                        + rs.getString(1) + ", "
                        + rs.getString(2) + ", "
                        + rs.getString(3) + ", "
                        + rs.getString(4));
            }

            rs.close();

            System.out.println("\nChecking read-through...");

            rs = RunScript.execute(dataSrc.getConnection(),
                    new StringReader("SELECT * from accounts where accountNumber=789;"));

            while (rs.next()) {
                System.out.println("    "
                        + rs.getString(1) + ", "
                        + rs.getString(2) + ", "
                        + rs.getString(3) + ", "
                        + rs.getString(4));
            }

            rs.close();
        } finally {
            System.out.println("\nDropping the cache...");

            try (
                    Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1:10800/");
                    Statement stmt = conn.createStatement()
            ) {
                stmt.executeUpdate("DROP CACHE accounts");
            }
        }
    }

    /**
     * POJO class that represents key.
     */
    static class AccountKey {
        int accountNumber;

        /**
         * Default constructor (required for deserialization).
         */
        @SuppressWarnings("unused")
        public AccountKey() {
        }

        public AccountKey(int accountNumber) {
            this.accountNumber = accountNumber;
        }
    }

    /**
     * POJO class that represents value.
     */
    static class Account {
        String firstName;
        String lastName;
        double balance;

        /**
         * Default constructor (required for deserialization).
         */
        @SuppressWarnings("unused")
        public Account() {
        }

        public Account(String firstName, String lastName, double balance) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.balance = balance;
        }
    }
}
