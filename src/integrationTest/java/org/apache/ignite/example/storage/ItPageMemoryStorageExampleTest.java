/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.storage;

import static org.apache.ignite.example.ExampleTestUtils.assertConsoleOutputContains;

import org.apache.ignite.example.AbstractExamplesTest;
import org.apache.ignite.internal.storage.pagememory.PersistentPageMemoryStorageEngine;
import org.apache.ignite.internal.storage.pagememory.VolatilePageMemoryStorageEngine;
import org.junit.jupiter.api.Test;

/**
 * For testing examples demonstrating work with {@link VolatilePageMemoryStorageEngine} and {@link PersistentPageMemoryStorageEngine}.
 */
public class ItPageMemoryStorageExampleTest extends AbstractExamplesTest {
    @Test
    public void testPersistentExample() throws Exception {
        assertConsoleOutputContains(PersistentPageMemoryStorageExample::main, EMPTY_ARGS,
                "\nAll accounts:\n"
                        + "    1, John, Doe, 1000.0\n"
                        + "    2, Jane, Roe, 2000.0\n"
                        + "    3, Mary, Major, 1500.0\n"
                        + "    4, Richard, Miles, 1450.0\n"
        );
    }

    @Test
    public void testInMemoryExample() throws Exception {
        assertConsoleOutputContains(VolatilePageMemoryStorageExample::main, EMPTY_ARGS,
                "\nAll accounts:\n"
                        + "    1, John, Doe, 1000.0\n"
                        + "    2, Jane, Roe, 2000.0\n"
                        + "    3, Mary, Major, 1500.0\n"
                        + "    4, Richard, Miles, 1450.0\n"
        );
    }
}
