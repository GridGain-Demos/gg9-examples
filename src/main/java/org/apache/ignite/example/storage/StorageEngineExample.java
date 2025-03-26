/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for executing examples that demonstrate working with different storage engines.
 */
class StorageEngineExample {
    private final String storageProfileName;

    /**
     * Creates an instance of the example runner.
     *
     * @param storageProfileName Name of the storage profile, that created SQL tables will use.
     */
    StorageEngineExample(String storageProfileName) {
        this.storageProfileName = storageProfileName;
    }

    /**
     * Executes the example.
     *
     * @throws SQLException In any of the SQL statements fail.
     */
    void run() throws SQLException {
        //--------------------------------------------------------------------------------------
        //
        // Creating a JDBC connection to connect to the cluster.
        //
        //--------------------------------------------------------------------------------------

        System.out.println("\nConnecting to server...");

        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1:10800/")) {
            //--------------------------------------------------------------------------------------
            //
            // Creating table.
            //
            //--------------------------------------------------------------------------------------

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(
                        "CREATE ZONE ACCOUNTS_ZONE "
                                + " WITH STORAGE_PROFILES='" + storageProfileName + "'"
                );
                stmt.executeUpdate(
                        "CREATE TABLE ACCOUNTS ( "
                                + "ACCOUNT_ID INT PRIMARY KEY,"
                                + "FIRST_NAME VARCHAR, "
                                + "LAST_NAME  VARCHAR, "
                                + "BALANCE    DOUBLE) "
                                + "ZONE ACCOUNTS_ZONE"
                );
            }

            //--------------------------------------------------------------------------------------
            //
            // Populating 'ACCOUNTS' table.
            //
            //--------------------------------------------------------------------------------------

            System.out.println("\nPopulating 'ACCOUNTS' table...");

            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO ACCOUNTS (ACCOUNT_ID, FIRST_NAME, LAST_NAME, BALANCE) values (?, ?, ?, ?)"
            )) {
                stmt.setInt(1, 1);
                stmt.setString(2, "John");
                stmt.setString(3, "Doe");
                stmt.setDouble(4, 1000.0d);
                stmt.executeUpdate();

                stmt.setInt(1, 2);
                stmt.setString(2, "Jane");
                stmt.setString(3, "Roe");
                stmt.setDouble(4, 2000.0d);
                stmt.executeUpdate();

                stmt.setInt(1, 3);
                stmt.setString(2, "Mary");
                stmt.setString(3, "Major");
                stmt.setDouble(4, 1500.0d);
                stmt.executeUpdate();

                stmt.setInt(1, 4);
                stmt.setString(2, "Richard");
                stmt.setString(3, "Miles");
                stmt.setDouble(4, 1450.0d);
                stmt.executeUpdate();
            }

            //--------------------------------------------------------------------------------------
            //
            // Requesting information about all account owners.
            //
            //--------------------------------------------------------------------------------------

            System.out.println("\nAll accounts:");

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(
                        "SELECT ACCOUNT_ID, FIRST_NAME, LAST_NAME, BALANCE FROM ACCOUNTS ORDER BY ACCOUNT_ID"
                )) {
                    while (rs.next()) {
                        System.out.println("    "
                                + rs.getString(1) + ", "
                                + rs.getString(2) + ", "
                                + rs.getString(3) + ", "
                                + rs.getString(4));
                    }
                }
            }
        } finally {
            System.out.println("\nDropping the table...");

            try (
                    Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1:10800/");
                    Statement stmt = conn.createStatement()
            ) {
                stmt.executeUpdate("DROP TABLE ACCOUNTS");
                stmt.executeUpdate("DROP ZONE ACCOUNTS_ZONE");
            }
        }
    }
}
