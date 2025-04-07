/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.compute;

import static org.apache.ignite.example.ExampleTestUtils.assertConsoleOutputContains;

import org.apache.ignite.example.AbstractExamplesTest;
import org.apache.ignite.example.ExampleTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * These tests check that all table examples pass correctly.
 */
public class ItComputeExamplesTest extends AbstractExamplesTest {
    /**
     * Deploy the unit.
     */
    @BeforeEach
    public void deployUnit() throws Exception {
        assert node != null;

        ExampleTestUtils.deployDummyUnit("computeExampleUnit", workDir, node);
    }

    /**
     * Runs {@link ComputeAsyncExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testComputeAsyncExample() throws Exception {
        assertConsoleOutputContains(ComputeAsyncExample::main, EMPTY_ARGS,
                "\nTotal number of characters in the words is '30'.");
    }

    /**
     * Runs {@link ComputeBroadcastExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testComputeBroadcastExample() throws Exception {
        assertConsoleOutputContains(ComputeBroadcastExample::main, EMPTY_ARGS,
                "\nCompute job executed...");
    }

    /**
     * Runs {@link ComputeCancellationExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testComputeCancellationExample() throws Exception {
        assertConsoleOutputContains(ComputeCancellationExample::main, EMPTY_ARGS,
                "\nThe compute job was cancelled:");
    }

    /**
     * Runs {@link ComputeColocatedExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testComputeColocatedExample() throws Exception {
        assertConsoleOutputContains(ComputeColocatedExample::main, EMPTY_ARGS,
                "\nDropping the table...");
    }

    /**
     * Runs {@link ComputeExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testComputeExample() throws Exception {
        assertConsoleOutputContains(ComputeExample::main, EMPTY_ARGS,
                "\nExecuting compute job for word 'runnable'...");
    }

    /**
     * Runs {@link ComputeJobPriorityExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testComputeJobPriorityExample() throws Exception {
        assertConsoleOutputContains(ComputeJobPriorityExample::main, EMPTY_ARGS,
                "\nExecuting compute jobs for arg '99'...");
    }

    /**
     * Runs {@link ComputeMapReduceExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testComputeMapReduceExample() throws Exception {
        assertConsoleOutputContains(ComputeMapReduceExample::main, EMPTY_ARGS,
                "\nTotal number of characters in the words is '29'.");
    }

    /**
     * Runs {@link ComputeWithCustomResultMarshallerExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testComputeWithCustomResultMarshallerExample() throws Exception {
        assertConsoleOutputContains(ComputeWithCustomResultMarshallerExample::main, EMPTY_ARGS,
                "\nThe length of the word 'job' is 3.");
    }

    /**
     * Runs {@link ComputeWithResultExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testComputeWithResultExample() throws Exception {
        assertConsoleOutputContains(ComputeWithResultExample::main, EMPTY_ARGS,
                "\nTotal number of words in the phrase is '4'.");
    }
}
