/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.streaming;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.sql.ResultSet;
import org.apache.ignite.sql.SqlRow;
import org.apache.ignite.table.DataStreamerItem;
import org.apache.ignite.table.DataStreamerOperationType;
import org.apache.ignite.table.DataStreamerOptions;
import org.apache.ignite.table.DataStreamerTarget;
import org.apache.ignite.table.RecordView;
import org.apache.ignite.table.Tuple;

/**
 * This example demonstrates the usage of the {@link DataStreamerTarget#streamData(Publisher, DataStreamerOptions)} API
 * with the {@link RecordView}.
 *
 * <p>Find instructions on how to run the example in the README.md file located in the "examples" directory root.
 */
public class RecordViewDataStreamerExample {
    /** Number of accounts to load. */
    private static final int ACCOUNTS_COUNT = 100_000;

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

        System.out.println("\nCreating 'accounts' table...");

        try (
                Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1:10800/");
                Statement stmt = conn.createStatement()
        ) {
            stmt.executeUpdate(
                    "CREATE TABLE accounts ("
                            + "accountNumber INT PRIMARY KEY,"
                            + "name          VARCHAR,"
                            + "balance       BIGINT,"
                            + "active        BOOLEAN)"
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

            RecordView<Tuple> view = client.tables().table("accounts").recordView();

            //--------------------------------------------------------------------------------------
            //
            // Streaming data using DataStreamerOperationType#PUT operation type.
            //
            //--------------------------------------------------------------------------------------
            streamAccountData(view, client, false);

            //--------------------------------------------------------------------------------------
            //
            // Streaming data using DataStreamerOperationType#REMOVE operation type.
            //
            //--------------------------------------------------------------------------------------
            streamAccountData(view, client, true);
        } finally {
            //--------------------------------------------------------------------------------------
            //
            // Dropping the table.
            //
            //--------------------------------------------------------------------------------------

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
     * Streams {@link RecordViewDataStreamerExample#ACCOUNTS_COUNT} accounts using provided table view.
     *
     * @param view The table view.
     * @param client The client.
     * @param remove If true the {@link DataStreamerOperationType#REMOVE} operation will be performed.
     */
    private static void streamAccountData(RecordView<Tuple> view, IgniteClient client, boolean remove) {
        //--------------------------------------------------------------------------------------
        //
        // Creating publisher.
        //
        //--------------------------------------------------------------------------------------

        System.out.println("\nCreating publisher...");

        long start = System.currentTimeMillis();

        CompletableFuture<Void> streamerFut;

        try (var publisher = new SubmissionPublisher<DataStreamerItem<Tuple>>()) {
            //--------------------------------------------------------------------------------------
            //
            // Configuring data streamer.
            //
            //--------------------------------------------------------------------------------------

            System.out.println("\nConfiguring data streamer...");

            DataStreamerOptions options = DataStreamerOptions.builder()
                    .pageSize(1000)
                    .perPartitionParallelOperations(1)
                    .autoFlushInterval(1000)
                    .retryLimit(16)
                    .build();

            streamerFut = view.streamData(publisher, options);

            //--------------------------------------------------------------------------------------
            //
            // Performing the 'streaming' operation.
            //
            //--------------------------------------------------------------------------------------

            System.out.println("\nStreaming account data using "
                    + (remove ? DataStreamerOperationType.REMOVE : DataStreamerOperationType.PUT)
                    + " operation type...");

            ThreadLocalRandom rnd = ThreadLocalRandom.current();

            for (int i = 0; i < ACCOUNTS_COUNT; i++) {
                Tuple entry = remove
                        ? Tuple.create().set("accountNumber", i)
                        : Tuple.create()
                            .set("accountNumber", i)
                            .set("name", "name" + i)
                            .set("balance", rnd.nextLong(100_000))
                            .set("active", rnd.nextBoolean());

                publisher.submit(remove ? DataStreamerItem.removed(entry) : DataStreamerItem.of(entry));

                if (i > 0 && i % 10_000 == 0) {
                    System.out.println("Streamed " + i + " accounts.");
                }
            }
        }

        //--------------------------------------------------------------------------------------
        //
        // Waiting for data to be flushed.
        //
        //--------------------------------------------------------------------------------------

        streamerFut.join();

        long end = System.currentTimeMillis();

        System.out.println("\nStreamed " + ACCOUNTS_COUNT + " accounts in " + (end - start) + "ms.");

        //--------------------------------------------------------------------------------------
        //
        // Checking the entries count.
        //
        //--------------------------------------------------------------------------------------

        try (ResultSet<SqlRow> rs = client.sql().execute(null, "SELECT count(*) FROM accounts")) {
            assert rs.hasNext();

            long entriesCnt = rs.next().longValue(0);

            System.out.println("\nThe current number of entries in the 'accounts' table: " + entriesCnt);
        }
    }
}
