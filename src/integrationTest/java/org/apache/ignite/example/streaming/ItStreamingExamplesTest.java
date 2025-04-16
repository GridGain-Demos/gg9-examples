/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.streaming;

import static org.apache.ignite.example.ExampleTestUtils.assertConsoleOutputContains;

import org.apache.ignite.example.AbstractExamplesTest;
import org.junit.jupiter.api.Test;

/**
 * These tests check that all table examples pass correctly.
 */
public class ItStreamingExamplesTest extends AbstractExamplesTest {
    /**
     * Runs {@link KeyValueViewDataStreamerExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testKeyValueViewDataStreamerExample() throws Exception {
        assertConsoleOutputContains(KeyValueViewDataStreamerExample::main, EMPTY_ARGS,
                "\nThe current number of entries in the 'accounts' table: 0");
    }

    /**
     * Runs {@link KeyValueViewPojoDataStreamerExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testKeyValueViewPojoDataStreamerExample() throws Exception {
        assertConsoleOutputContains(KeyValueViewPojoDataStreamerExample::main, EMPTY_ARGS,
                "\nThe current number of entries in the 'accounts' table: 0");
    }

    /**
     * Runs {@link RecordViewDataStreamerExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testRecordViewDataStreamerExample() throws Exception {
        assertConsoleOutputContains(RecordViewDataStreamerExample::main, EMPTY_ARGS,
                "\nThe current number of entries in the 'accounts' table: 0");
    }

    /**
     * Runs {@link RecordViewPojoDataStreamerExample} and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testRecordViewPojoDataStreamerExample() throws Exception {
        assertConsoleOutputContains(RecordViewPojoDataStreamerExample::main, EMPTY_ARGS,
                "\nThe current number of entries in the 'accounts' table: 0");
    }
}
