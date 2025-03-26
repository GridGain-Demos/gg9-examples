/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.continuousquery;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;
import org.apache.ignite.table.ContinuousQueryWatermark;
import org.apache.ignite.table.TableRowEvent;
import org.apache.ignite.table.TableRowEventBatch;
import org.apache.ignite.table.Tuple;

/**
 * Custom subscriber to be used in the Continuous Query examples.
 */
public class ContinuousQueryCustomSubscriber implements Flow.Subscriber<TableRowEventBatch<Tuple>> {
    /** Subscriber future. */
    private final CompletableFuture<Void> subscriberFut;

    /** Event count latch. */
    private final CountDownLatch eventCountLatch;

    /** Subscription. */
    private Subscription subscription;

    /** Last event watermark. */
    private ContinuousQueryWatermark lastEventWatermark;

    /**
     * Constructor.
     *
     * @param expectedEvents Number of expected events.
     */
    ContinuousQueryCustomSubscriber(int expectedEvents) {
        this.eventCountLatch = new CountDownLatch(expectedEvents);

        this.subscriberFut = new CompletableFuture<>();
    }

    /** {@inheritDoc} */
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        subscription.request(Long.MAX_VALUE);

        this.subscription = subscription;
    }

    /** {@inheritDoc} */
    @Override
    public void onNext(TableRowEventBatch<Tuple> batch) {
        List<TableRowEvent<Tuple>> items = batch.rows();

        for (TableRowEvent<Tuple> item: items) {
            System.out.println("Event [type=" + item.type() + ", oldEntry=" + item.oldEntry() + ", newEntry=" + item.entry() + "]");

            this.lastEventWatermark = item.watermark();

            eventCountLatch.countDown();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onError(Throwable throwable) {
        System.out.println("An error occurred during processing: " + throwable.getMessage());

        subscriberFut.completeExceptionally(throwable);
    }

    /** {@inheritDoc} */
    @Override
    public void onComplete() {
        System.out.println("Processing completed.");

        subscriberFut.complete(null);
    }

    /** Get subscriber future. */
    CompletableFuture<Void> getSubscriberFut() {
        return subscriberFut;
    }

    /** Get event count latch. */
    CountDownLatch getEventCountLatch() {
        return eventCountLatch;
    }

    /** Get subscription. */
    Subscription getSubscription() {
        return subscription;
    }

    /** Get last event watermark. */
    ContinuousQueryWatermark getLastEventWatermark() {
        return lastEventWatermark;
    }
}
