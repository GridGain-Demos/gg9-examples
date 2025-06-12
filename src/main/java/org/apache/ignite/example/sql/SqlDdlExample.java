/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.sql;

import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.sql.BatchedArguments;
import org.apache.ignite.sql.ResultSet;
import org.apache.ignite.sql.SqlRow;

/**
 * Example to showcase DDL capabilities of SQL engine.
 *
 * <p>Find instructions on how to run the example in the README.md file located in the "examples" directory root.
 */
public class SqlDdlExample {

    /**
     * Main method of the example.
     *
     * @param args The command line arguments.
     * @throws Exception If failed.
     */
    public static void main(String[] args) throws Exception {
        try (IgniteClient client = IgniteClient.builder()
                .addresses("127.0.0.1:10800")
                .build()
        ) {
            //--------------------------------------------------------------------------------------
            //
            // Drop data if exists.
            //
            //--------------------------------------------------------------------------------------
            client.sql().executeScript("DROP INDEX IF EXISTS PersonIdx; "
                    + "DROP TABLE IF EXISTS Person; "
                    + "DROP TABLE IF EXISTS City; "
                    + "DROP ZONE IF EXISTS ReplicatedZone; "

                    //--------------------------------------------------------------------------------------
                    //
                    // Create zone with replicas=2.
                    //
                    //--------------------------------------------------------------------------------------

                    + "CREATE ZONE IF NOT EXISTS ReplicatedZone WITH STORAGE_PROFILES='default', REPLICAS=2; "

                    //--------------------------------------------------------------------------------------
                    //
                    // Create reference City table in the zone with replicas=2.
                    //
                    //--------------------------------------------------------------------------------------

                    + "CREATE TABLE City (id INT PRIMARY KEY, name VARCHAR) ZONE ReplicatedZone; "

                    //--------------------------------------------------------------------------------------
                    //
                    // Create table in the zone with replicas=2.
                    //
                    //--------------------------------------------------------------------------------------

                    + "CREATE TABLE Person (id INT, name VARCHAR, city_id INT, PRIMARY KEY (id, city_id)) "
                    + "COLOCATE BY (city_id) ZONE ReplicatedZone; "

                    //--------------------------------------------------------------------------------------
                    //
                    // Create an index.
                    //
                    //--------------------------------------------------------------------------------------

                    + "CREATE INDEX PersonIdx ON Person (city_id)");

            System.out.println("\nDatabase objects have been created.");

            client.sql().executeBatch(null, "INSERT INTO City (id, name) VALUES (?, ?)",
                    BatchedArguments
                            .of(1L, "Forest Hill")
                            .add(2L, "Denver")
                            .add(3L, "St. Petersburg")

            );

            client.sql().executeBatch(null, "INSERT INTO Person (id, name, city_id) values (?, ?, ?)",
                    BatchedArguments
                            .of(1L, "John Doe", 3L)
                            .add(2L, "Jane Roe", 2L)
                            .add(3L, "Mary Major", 1L)
                            .add(4L, "Richard Miles", 2L)
            );

            System.out.println("\nData has been populated.");

            try (ResultSet<SqlRow> res = client.sql().execute(null,
                    "SELECT p.name, c.name FROM Person p INNER JOIN City c ON c.id = p.city_id")) {

                System.out.println("\nQuery results:");

                while (res.hasNext()) {
                    SqlRow row = res.next();

                    System.out.printf("%s, %s%n", row.value(0), row.value(1));
                }
            }

            client.sql().executeScript("DROP TABLE Person; "
                    + "DROP TABLE City;");

            System.out.println("\nDatabase objects have been dropped.");
        }

        System.out.println("\nCache query DDL example has finished.");
    }
}
