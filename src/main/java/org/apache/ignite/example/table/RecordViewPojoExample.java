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

/**
 * This example demonstrates the usage of the {@link RecordView} API with user-defined POJOs.
 *
 * <p>Find instructions on how to run the example in the README.md file located in the "examples" directory root.
 */
public class RecordViewPojoExample {
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

            RecordView<Account> accounts = client.tables()
                    .table("accounts")
                    .recordView(Account.class);

            //--------------------------------------------------------------------------------------
            //
            // Performing the 'insert' operation.
            //
            //--------------------------------------------------------------------------------------

            System.out.println("\nInserting a record into the 'accounts' table...");

            Account newAccount = new Account(
                    123456,
                    "Val",
                    "Kulichenko",
                    100.00d
            );

            accounts.insert(null, newAccount);

            //--------------------------------------------------------------------------------------
            //
            // Performing the 'get' operation.
            //
            //--------------------------------------------------------------------------------------

            System.out.println("\nRetrieving a record using RecordView API...");

            Account account = accounts.get(null, new Account(123456));

            System.out.println(
                    "\nRetrieved record:\n"
                        + "    Account Number: " + account.accountNumber + '\n'
                        + "    Owner: " + account.firstName + " " + account.lastName + '\n'
                        + "    Balance: $" + account.balance);
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

    /**
     * POJO class that represents record.
     */
    static class Account {
        int accountNumber;
        String firstName;
        String lastName;
        double balance;

        /**
         * Default constructor (required for deserialization).
         */
        @SuppressWarnings("unused")
        public Account() {
        }

        public Account(int accountNumber) {
            this.accountNumber = accountNumber;

            firstName = null;
            lastName = null;
            balance = 0.0d;
        }

        public Account(int accountNumber, String firstName, String lastName, double balance) {
            this.accountNumber = accountNumber;
            this.firstName = firstName;
            this.lastName = lastName;
            this.balance = balance;
        }
    }
}
