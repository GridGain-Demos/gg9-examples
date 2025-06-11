/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.sql;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * This example demonstrates usage of COPY command via JDBC driver.
 *
 * <p>Find instructions on how to run the example in the README.md file located in the "examples" directory root.
 */
public class SqlJdbcCopyExample {
    /**
     * Main method of the example.
     *
     * @param args The command line arguments.
     * @throws Exception If failed.
     */
    public static void main(String[] args) throws Exception {

        //--------------------------------------------------------------------------------------
        //
        // Creating a JDBC connection to the cluster.
        //
        //--------------------------------------------------------------------------------------

        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1:10800")
        ) {
            //--------------------------------------------------------------------------------------
            //
            // Dropping 'City' table if exists.
            //
            //--------------------------------------------------------------------------------------

            executeCommand(conn, "DROP TABLE IF EXISTS City");

            //--------------------------------------------------------------------------------------
            //
            // Creating 'City' table.
            //
            //--------------------------------------------------------------------------------------

            executeCommand(conn, "CREATE TABLE City ("
                    + "    ID INT PRIMARY KEY, "
                    + "    Name VARCHAR(35), "
                    + "    CountryCode VARCHAR(3), "
                    + "    District VARCHAR(20), "
                    + "    Population INT"
                    + ")"
            );

            System.out.println("\nCreated database objects.");

            //--------------------------------------------------------------------------------------
            //
            // Loading data from CSV file.
            //
            //--------------------------------------------------------------------------------------

            executeCommand(conn,
                    String.format("COPY FROM '%s' INTO City (ID, Name, CountryCode, District, Population) FORMAT CSV",
                            resolveAbsolutePath("sql/city.csv")));

            //--------------------------------------------------------------------------------------
            //
            // Reading data from 'City' table.
            //
            //--------------------------------------------------------------------------------------

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM City")) {
                    rs.next();

                    System.out.printf("%nPopulated City table: %d entries%n", rs.getLong(1));
                }
            }

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT Name, CountryCode FROM City WHERE ID=5")) {
                    rs.next();

                    System.out.printf("%nCity with ID=5: %s, %s%n", rs.getString(1), rs.getString(2));
                }
            }

            //--------------------------------------------------------------------------------------
            //
            // Dropping 'City' table.
            //
            //--------------------------------------------------------------------------------------

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DROP TABLE City");
            }

            System.out.println("\nDropped database objects.");
        }
    }

    /**
     * Resolve absolute path to the resource file.
     *
     * @param resourceName Resource file path relative to `resources` directory.
     * @return Absolute path to the resource file.
     * @throws URISyntaxException If path is not valid.
     */
    private static String resolveAbsolutePath(String resourceName) throws URISyntaxException {
        URL res = SqlJdbcCopyExample.class.getClassLoader().getResource(resourceName);
        File file = Paths.get(res.toURI()).toFile();
        return file.getAbsolutePath();
    }

    /**
     * Execute SQL command.
     *
     * @param conn Connection.
     * @param sql SQL statement.
     * @throws Exception If failed.
     */
    private static void executeCommand(Connection conn, String sql) throws Exception {
        System.out.printf("%nExecuting: %s%n", sql);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}
