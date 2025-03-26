/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Example test utilities.
 */
public class ExampleTestUtils {
    /**
     * Interface for a general example main function.
     */
    @FunctionalInterface
    public interface ExampleConsumer {
        void accept(String[] args) throws Exception;
    }

    /**
     * Capture console output of the example.
     *
     * @param consumer Method which output should be captured. Ordinary main of the example.
     * @param args     Arguments.
     * @return Captured output as a string.
     */
    public static String captureConsole(ExampleConsumer consumer, String[] args) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outStream);

        PrintStream old = System.out;

        try {
            System.setOut(printStream);

            consumer.accept(args);

            System.out.flush();
        } finally {
            System.setOut(old);
        }

        return outStream.toString(UTF_8);
    }

    /**
     * Assert that console output of the example equals expected.
     *
     * @param consumer Method which output should be captured. Ordinary main of the example.
     * @param args     Arguments.
     * @param expected Expected console output.
     */
    public static void assertConsoleOutput(ExampleConsumer consumer, String[] args, String expected) throws Exception {
        String captured = ExampleTestUtils.captureConsole(consumer, args);

        captured = captured.replaceAll("\r", "");

        assertEquals(expected, captured);
    }

    /**
     * Assert that console output of the example equals expected.
     *
     * @param consumer Method which output should be captured. Ordinary main of the example.
     * @param args Arguments.
     * @param expected Expected console output.
     */
    public static void assertConsoleOutputContains(
            ExampleConsumer consumer,
            String[] args,
            String... expected
    ) throws Exception {
        String captured = ExampleTestUtils.captureConsole(consumer, args);

        captured = captured.replaceAll("\r", "");

        for (String single : expected) {
            assertThat(captured, containsString(single));
        }
    }
}
