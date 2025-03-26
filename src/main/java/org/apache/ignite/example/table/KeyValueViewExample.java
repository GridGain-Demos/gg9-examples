/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.table.KeyValueView;
import org.apache.ignite.table.Tuple;

/**
 * This example demonstrates the usage of the {@link KeyValueView} API.
 *
 * <p>Find instructions on how to run the example in the README.md file located in the "examples" directory root.
 */
public class KeyValueViewExample {
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
            stmt.executeUpdate(
                    "CREATE TABLE accounts ("
                            + "accountNumber INT PRIMARY KEY,"
                            + "firstName     VARCHAR,"
                            + "lastName      VARCHAR,"
                            + "balance       DOUBLE)"
            );
        }

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
            // Creating a key-value view for the 'accounts' table.
            //
            //--------------------------------------------------------------------------------------

            KeyValueView<Tuple, Tuple> kvView = client.tables().table("accounts").keyValueView();

            //--------------------------------------------------------------------------------------
            //
            // Performing the 'put' operation.
            //
            //--------------------------------------------------------------------------------------

            System.out.println("\nInserting a key-value pair into the 'accounts' table...");

            Tuple key = Tuple.create()
                    .set("accountNumber", 123456);

            Tuple value = Tuple.create()
                    .set("firstName", "Val")
                    .set("lastName", "Kulichenko")
                    .set("balance", 100.00d);

            kvView.put(null, key, value);

            //--------------------------------------------------------------------------------------
            //
            // Performing the 'get' operation.
            //
            //--------------------------------------------------------------------------------------

            System.out.println("\nRetrieving a value using KeyValueView API...");

            value = kvView.get(null, key);

            System.out.println(
                    "\nRetrieved value:\n"
                            + "    Account Number: " + key.intValue("accountNumber") + '\n'
                            + "    Owner: " + value.stringValue("firstName") + " " + value.stringValue("lastName") + '\n'
                            + "    Balance: $" + value.doubleValue("balance"));
        } finally {
            System.out.println("\nDropping the table...");

            try (
                    Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1:10800/");
                    Statement stmt = conn.createStatement()
            ) {
                stmt.executeUpdate("DROP TABLE accounts");
            }
        }
    }
}
