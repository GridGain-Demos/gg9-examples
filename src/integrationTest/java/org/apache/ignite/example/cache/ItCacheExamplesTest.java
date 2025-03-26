/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.cache;

import static org.apache.ignite.example.ExampleTestUtils.assertConsoleOutputContains;

import org.apache.ignite.example.AbstractExamplesTest;
import org.junit.jupiter.api.Test;

/**
 * These tests check that all table examples pass correctly.
 */
public class ItCacheExamplesTest extends AbstractExamplesTest {
    /**
     * Runs KeyValueViewExample and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testKeyValueViewExample() throws Exception {
        assertConsoleOutputContains(KeyValueViewExample::main, EMPTY_ARGS,
                "\nRetrieved value:\n"
                        + "    Account Number: 123456\n"
                        + "    Owner: Val Kulichenko\n"
                        + "    Balance: $100.0\n");
    }

    /**
     * Runs KeyValueViewPojoExample and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testKeyValueViewPojoExample() throws Exception {
        assertConsoleOutputContains(KeyValueViewPojoExample::main, EMPTY_ARGS,
                "\nRetrieved value:\n"
                        + "    Account Number: 123456\n"
                        + "    Owner: Val Kulichenko\n"
                        + "    Balance: $100.0\n");
    }
}
