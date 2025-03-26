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
import org.apache.ignite.table.RecordView;
import org.apache.ignite.table.Tuple;

/**
 * This example demonstrates the usage of the {@link RecordView} API.
 *
 * <p>Find instructions on how to run the example in the README.md file located in the "examples" directory root.
 */
public class RecordViewExample {
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
            // Creating a record view for the 'accounts' table.
            //
            //--------------------------------------------------------------------------------------

            RecordView<Tuple> accounts = client.tables().table("accounts").recordView();

            //--------------------------------------------------------------------------------------
            //
            // Performing the 'insert' operation.
            //
            //--------------------------------------------------------------------------------------

            System.out.println("\nInserting a record into the 'accounts' table...");

            Tuple newAccountTuple = Tuple.create()
                    .set("accountNumber", 123456)
                    .set("firstName", "Val")
                    .set("lastName", "Kulichenko")
                    .set("balance", 100.00d);

            accounts.insert(null, newAccountTuple);

            //--------------------------------------------------------------------------------------
            //
            // Performing the 'get' operation.
            //
            //--------------------------------------------------------------------------------------

            System.out.println("\nRetrieving a record using RecordView API...");

            Tuple accountNumberTuple = Tuple.create().set("accountNumber", 123456);

            Tuple accountTuple = accounts.get(null, accountNumberTuple);

            System.out.println(
                    "\nRetrieved record:\n"
                            + "    Account Number: " + accountTuple.intValue("accountNumber") + '\n'
                            + "    Owner: " + accountTuple.stringValue("firstName") + " " + accountTuple.stringValue("lastName") + '\n'
                            + "    Balance: $" + accountTuple.doubleValue("balance"));
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
