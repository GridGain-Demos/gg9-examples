/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.continuousquery;

import org.apache.ignite.example.AbstractExamplesTest;
import org.junit.jupiter.api.Test;

import static org.apache.ignite.example.ExampleTestUtils.assertConsoleOutputContains;

/**
 * These tests check that all table examples pass correctly.
 */
public class ItContinuousQueryExamplesTest extends AbstractExamplesTest {
    /**
     * Runs {@link ContinuousQueryExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testContinuousQueryExample() throws Exception {
        assertConsoleOutputContains(ContinuousQueryExample::main, EMPTY_ARGS,
                "\nDropping the tables...");
    }

    /**
     * Runs {@link ContinuousQueryResumeFromWatermarkExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testContinuousQueryResumeFromWatermarkExample() throws Exception {
        assertConsoleOutputContains(ContinuousQueryResumeFromWatermarkExample::main, EMPTY_ARGS,
                "\nDropping the tables...");
    }

    /**
     * Runs {@link ContinuousQueryTransactionsExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testContinuousQueryTransactionsExample() throws Exception {
        assertConsoleOutputContains(ContinuousQueryTransactionsExample::main, EMPTY_ARGS,
                "\nDropping the tables...");
    }
}
