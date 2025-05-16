/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * This example demonstrates the usage of JDBC driver.
 *
 * <p>Find instructions on how to run the example in the README.md file located in the "examples" directory root.
 */
public class SqlJdbcExample {
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
            // Create database objects.
            //
            //--------------------------------------------------------------------------------------

            try (Statement stmt = conn.createStatement()) {


                //--------------------------------------------------------------------------------------
                //
                // Drop data if exists.
                //
                //--------------------------------------------------------------------------------------
                stmt.executeUpdate("DROP INDEX IF EXISTS Person_idx");
                stmt.executeUpdate("DROP TABLE IF EXISTS Person");
                stmt.executeUpdate("DROP TABLE IF EXISTS City");
                stmt.executeUpdate("DROP ZONE IF EXISTS sqlJdbcReplica2");

                //--------------------------------------------------------------------------------------
                //
                // Create ZONE wth replica=2.
                //
                //--------------------------------------------------------------------------------------

                stmt.executeUpdate("CREATE ZONE IF NOT EXISTS sqlJdbcReplica2 WITH STORAGE_PROFILES='default', replicas=2");

                //--------------------------------------------------------------------------------------
                //
                // Create reference City table in the zone with replica=2.
                //
                //--------------------------------------------------------------------------------------

                stmt.executeUpdate("CREATE TABLE city (id INT PRIMARY KEY, name VARCHAR) ZONE sqlJdbcReplica2");

                //--------------------------------------------------------------------------------------
                //
                // Create table in the zone with replica=2.
                //
                //--------------------------------------------------------------------------------------

                stmt.executeUpdate("CREATE TABLE person (id INT, name VARCHAR, city_id INT, "
                        + "PRIMARY KEY (id, city_id)) COLOCATE BY (city_id) ZONE sqlJdbcReplica2");

                //--------------------------------------------------------------------------------------
                //
                // Create an index.
                //
                //--------------------------------------------------------------------------------------

                stmt.executeUpdate("CREATE INDEX Person_idx on Person (city_id)");
            }

            System.out.println("\nCreated database objects.");

            //--------------------------------------------------------------------------------------
            //
            // Populate City table with PreparedStatement.
            //
            //--------------------------------------------------------------------------------------

            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO city (id, name) VALUES (?, ?)")) {
                stmt.setLong(1, 1L);
                stmt.setString(2, "Forest Hill");
                stmt.executeUpdate();

                stmt.setLong(1, 2L);
                stmt.setString(2, "Denver");
                stmt.executeUpdate();

                stmt.setLong(1, 3L);
                stmt.setString(2, "St. Petersburg");
                stmt.executeUpdate();
            }

            //--------------------------------------------------------------------------------------
            //
            // Populate Person table with PreparedStatement.
            //
            //--------------------------------------------------------------------------------------

            try (PreparedStatement stmt =
                    conn.prepareStatement("INSERT INTO person (id, name, city_id) values (?, ?, ?)")) {
                stmt.setLong(1, 1L);
                stmt.setString(2, "John Doe");
                stmt.setLong(3, 3L);
                stmt.executeUpdate();

                stmt.setLong(1, 2L);
                stmt.setString(2, "Jane Roe");
                stmt.setLong(3, 2L);
                stmt.executeUpdate();

                stmt.setLong(1, 3L);
                stmt.setString(2, "Mary Major");
                stmt.setLong(3, 1L);
                stmt.executeUpdate();

                stmt.setLong(1, 4L);
                stmt.setString(2, "Richard Miles");
                stmt.setLong(3, 2L);
                stmt.executeUpdate();
            }

            System.out.println("\n" + "Populated data.");

            //--------------------------------------------------------------------------------------
            //
            // Get data.
            //
            //--------------------------------------------------------------------------------------

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs =
                        stmt.executeQuery("SELECT p.name, c.name FROM Person p INNER JOIN City c on c.id = p.city_id")) {
                    System.out.println("\nQuery results:");

                    while (rs.next()) {
                        System.out.printf("%s, %s%n", rs.getString(1), rs.getString(2));
                    }
                }
            }

            //--------------------------------------------------------------------------------------
            //
            // Drop database objects.
            //
            //--------------------------------------------------------------------------------------

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DROP INDEX IF EXISTS Person_idx");
                stmt.executeUpdate("DROP TABLE IF EXISTS Person");
                stmt.executeUpdate("DROP TABLE IF EXISTS City");
                stmt.executeUpdate("DROP ZONE IF EXISTS sqlJdbcReplica2");
            }

            System.out.println("\nDropped database objects.");
        }
    }
}
