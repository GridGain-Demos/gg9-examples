/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.streaming.receiver;

import static org.apache.ignite.example.ExampleTestUtils.assertConsoleOutputContains;

import org.apache.ignite.example.AbstractExamplesTest;
import org.apache.ignite.example.ExampleTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * These tests check that all table examples pass correctly.
 */
public class ItReceiverExamplesTest extends AbstractExamplesTest {
    /**
     * Deploy the unit.
     */
    @BeforeEach
    public void deployUnit() throws Exception {
        assert node != null;

        ExampleTestUtils.deployDummyUnit("receiverExampleUnit", workDir, node);
    }

    /**
     * Runs {@link ReceiverStreamProcessingExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testReceiverStreamProcessingExample() throws Exception {
        assertConsoleOutputContains(ReceiverStreamProcessingExample::main, EMPTY_ARGS,
                "\nStreamed 10000 trades in ");
    }

    /**
     * Runs {@link ReceiverStreamProcessingWithResultSubscriberExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testReceiverStreamProcessingWithResultSubscriberExample() throws Exception {
        assertConsoleOutputContains(ReceiverStreamProcessingWithResultSubscriberExample::main, EMPTY_ARGS,
                "\nStreamed 10000 trades in ");
    }

    /**
     * Runs {@link ReceiverStreamProcessingWithTableUpdateExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testReceiverStreamProcessingWithTableUpdateExample() throws Exception {
        assertConsoleOutputContains(ReceiverStreamProcessingWithTableUpdateExample::main, EMPTY_ARGS,
                "\nCurrent account balances:");
    }
}
