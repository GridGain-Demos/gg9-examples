/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.sql;

import static org.apache.ignite.example.ExampleTestUtils.assertConsoleOutputContains;

import org.apache.ignite.example.AbstractExamplesTest;
import org.apache.ignite.example.sql.jdbc.SqlJdbcExample;
import org.junit.jupiter.api.Test;

/**
 * These tests check that all SQL JDBC examples pass correctly.
 */
public class ItSqlExamplesTest extends AbstractExamplesTest {
    /**
     * Runs SqlJdbcExample and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testSqlJdbcExample() throws Exception {
        assertConsoleOutputContains(SqlJdbcExample::main, EMPTY_ARGS,
                "\nAll accounts:\n"
                        + "    John, Doe, Forest Hill\n"
                        + "    Jane, Roe, Forest Hill\n"
                        + "    Mary, Major, Denver\n"
                        + "    Richard, Miles, St. Petersburg\n",

                "\nAccounts with balance lower than 1,500:\n"
                        + "    John, Doe, 1000.0\n"
                        + "    Richard, Miles, 1450.0\n",

                "\nAll accounts:\n"
                        + "    Jane, Roe, Forest Hill\n"
                        + "    Mary, Major, Denver\n"
                        + "    Richard, Miles, St. Petersburg\n"
        );
    }

    /**
     * Runs SqlApiExample and checks its output.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testSqlApiExample() throws Exception {
        assertConsoleOutputContains(SqlApiExample::main, EMPTY_ARGS,
                "\nAdded cities: 3",
                "\nAdded accounts: 4",

                "\nAll accounts:\n"
                        + "    John, Doe, Forest Hill\n"
                        + "    Jane, Roe, Forest Hill\n"
                        + "    Mary, Major, Denver\n"
                        + "    Richard, Miles, St. Petersburg\n",

                "\nAccounts with balance lower than 1,500:\n"
                        + "    John, Doe, 1000.0\n"
                        + "    Richard, Miles, 1450.0\n",

                "\nAll accounts:\n"
                        + "    Jane, Roe, Forest Hill\n"
                        + "    Mary, Major, Denver\n"
                        + "    Richard, Miles, St. Petersburg\n"
        );
    }
}
