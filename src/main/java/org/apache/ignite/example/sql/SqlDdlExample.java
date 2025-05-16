/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.sql;

import java.util.List;
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
            client.sql().executeScript("DROP INDEX IF EXISTS Person_idx");
            client.sql().executeScript("DROP TABLE IF EXISTS Person");
            client.sql().executeScript("DROP TABLE IF EXISTS City");
            client.sql().executeScript("DROP ZONE IF EXISTS sqlJdbcReplica2");

            //--------------------------------------------------------------------------------------
            //
            // Create ZONE wth replica=2.
            //
            //--------------------------------------------------------------------------------------

            client.sql().executeScript("CREATE ZONE IF NOT EXISTS sqlJdbcReplica2 WITH STORAGE_PROFILES='default', replicas=2");

            //--------------------------------------------------------------------------------------
            //
            // Create reference City table in the zone with replica=2.
            //
            //--------------------------------------------------------------------------------------

            client.sql().executeScript(
                    "CREATE TABLE city (id INT PRIMARY KEY, name VARCHAR) ZONE sqlJdbcReplica2");

            //--------------------------------------------------------------------------------------
            //
            // Create table in the zone with replica=2.
            //
            //--------------------------------------------------------------------------------------

            client.sql().executeScript(
                    "CREATE TABLE person (id INT, name VARCHAR, city_id INT, PRIMARY KEY (id, city_id)) "
                            + "COLOCATE BY (city_id) ZONE sqlJdbcReplica2");

            //--------------------------------------------------------------------------------------
            //
            // Create an index.
            //
            //--------------------------------------------------------------------------------------

            client.sql().executeScript("CREATE INDEX Person_idx on Person (city_id)");

            System.out.println("\nCreated database objects.");

            client.sql().executeBatch(null, "INSERT INTO city (id, name) VALUES (?, ?)",
                    BatchedArguments.from(
                            List.of(
                                    List.of(1L, "Forest Hill"),
                                    List.of(2L, "Denver"),
                                    List.of(3L, "St. Petersburg")
                            )
                    ));

            client.sql().executeBatch(null, "INSERT INTO person (id, name, city_id) values (?, ?, ?)",
                    BatchedArguments.from(
                            List.of(
                                    List.of(1L, "John Doe", 3L),
                                    List.of(2L, "Jane Roe", 2L),
                                    List.of(3L, "Mary Major", 1L),
                                    List.of(4L, "Richard Miles", 2L)
                            )));

            System.out.println("\nPopulated data.");

            try (ResultSet<SqlRow> res = client.sql().execute(null,
                    "SELECT p.name, c.name FROM Person p INNER JOIN City c on c.id = p.city_id")) {

                System.out.println("\nQuery results:");

                while (res.hasNext()) {
                    SqlRow row = res.next();

                    System.out.printf("%s, %s%n", row.value(0), row.value(1));
                }
            }

            client.sql().executeScript("drop table Person");
            client.sql().executeScript("drop table City");

            System.out.println("\nDropped database objects.");
        }

        System.out.println("\nCache query DDL example finished.");
    }
}
