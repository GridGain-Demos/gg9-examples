/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.continuousquery;

import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.table.ContinuousQueryOptions;
import org.apache.ignite.table.ContinuousQuerySource;
import org.apache.ignite.table.ContinuousQueryWatermark;
import org.apache.ignite.table.RecordView;
import org.apache.ignite.table.Tuple;

/**
 * This example demonstrates the usage of the {@link ContinuousQuerySource} API resuming from a specific watermark.
 *
 * <p>Find instructions on how to run the example in the README.md file located in the "examples" directory root.
 */
public class ContinuousQueryResumeFromWatermarkExample {
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
            // Creating 'accounts' table.
            //
            //--------------------------------------------------------------------------------------

            System.out.println("\nCreating 'accounts' table...");

            client.sql().executeScript(
                    "CREATE TABLE accounts ("
                            + "accountNumber INT PRIMARY KEY,"
                            + "name          VARCHAR,"
                            + "balance       BIGINT,"
                            + "active        BOOLEAN)"
            );

            //--------------------------------------------------------------------------------------
            //
            // Configuring continuous query.
            //
            //--------------------------------------------------------------------------------------

            RecordView<Tuple> view = client.tables().table("accounts").recordView();

            var subscriber = new ContinuousQueryCustomSubscriber(3);

            view.queryContinuously(subscriber, null);

            //--------------------------------------------------------------------------------------
            //
            // Performing 3 updates to generate events.
            //
            //--------------------------------------------------------------------------------------

            view.upsert(null, account(1, "Name"));
            view.upsert(null, account(1, "Updated name"));
            view.upsert(null, account(1, "Updated name 2"));

            //--------------------------------------------------------------------------------------
            //
            // Waiting for the events.
            //
            //--------------------------------------------------------------------------------------

            subscriber.getEventCountLatch().await();

            //--------------------------------------------------------------------------------------
            //
            // Cancelling the subscription.
            //
            //--------------------------------------------------------------------------------------

            subscriber.getSubscription().cancel();

            //--------------------------------------------------------------------------------------
            //
            // Waiting for subscriber future to be completed.
            //
            //--------------------------------------------------------------------------------------

            subscriber.getSubscriberFut().join();

            //--------------------------------------------------------------------------------------
            //
            // Getting watermark for the last event.
            //
            //--------------------------------------------------------------------------------------

            ContinuousQueryWatermark lastEventWatermark = subscriber.getLastEventWatermark();

            //--------------------------------------------------------------------------------------
            //
            // Performing 2 updates to generate events.
            //
            //--------------------------------------------------------------------------------------

            view.upsert(null, account(1, "Updated name 3"));
            view.upsert(null, account(1, "Updated name 4"));

            //--------------------------------------------------------------------------------------
            //
            // Configuring continuous query to resume processing from a specific watermark.
            //
            //--------------------------------------------------------------------------------------

            ContinuousQueryCustomSubscriber resumingSubscriber = new ContinuousQueryCustomSubscriber(2);

            ContinuousQueryOptions options = ContinuousQueryOptions.builder()
                    .watermark(lastEventWatermark)
                    .build();

            view.queryContinuously(resumingSubscriber, options);

            //--------------------------------------------------------------------------------------
            //
            // Waiting for the events.
            //
            //--------------------------------------------------------------------------------------

            resumingSubscriber.getEventCountLatch().await();

            //--------------------------------------------------------------------------------------
            //
            // Cancelling the subscription.
            //
            //--------------------------------------------------------------------------------------

            resumingSubscriber.getSubscription().cancel();

            //--------------------------------------------------------------------------------------
            //
            // Waiting for subscriber future to be completed.
            //
            //--------------------------------------------------------------------------------------

            resumingSubscriber.getSubscriberFut().join();

            //--------------------------------------------------------------------------------------
            //
            // Dropping the table.
            //
            //--------------------------------------------------------------------------------------

            System.out.println("\nDropping the tables...");

            client.sql().executeScript("DROP TABLE accounts");
        }
    }

    /**
     * Creates account Tuple.
     *
     * @param accountNumber Account number.
     * @param name Name.
     * @return Tuple.
     */
    private static Tuple account(int accountNumber, String name) {
        return Tuple.create()
                .set("accountNumber", accountNumber)
                .set("name", name)
                .set("balance", 0L)
                .set("active", true);
    }
}
