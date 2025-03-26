/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.tx;

import static org.apache.ignite.example.ExampleTestUtils.assertConsoleOutputContains;

import org.apache.ignite.example.AbstractExamplesTest;
import org.junit.jupiter.api.Test;

/**
 * Tests for transactional examples.
 */
public class ItTransactionsExamplesTest extends AbstractExamplesTest {
    /**
     * Runs TransactionsExample and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testTransactionsExample() throws Exception {
        assertConsoleOutputContains(TransactionsExample::main, EMPTY_ARGS,
                "Initial balance: 1000.0",
                "Balance after the sync transaction: 1200.0",
                "Balance after the async transaction: 1500.0");
    }
}
