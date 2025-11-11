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
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.apache.ignite.example.CompletableFutureMatcher.willBe;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.ignite.IgniteServer;
import org.apache.ignite.deployment.version.Version;
import org.apache.ignite.internal.app.IgniteImpl;
import org.apache.ignite.internal.app.IgniteServerImpl;
import org.apache.ignite.internal.deployunit.DeploymentUnit;
import org.apache.ignite.internal.deployunit.FilesDeploymentUnit;
import org.apache.ignite.internal.deployunit.InitialDeployMode;
import org.apache.ignite.internal.deployunit.NodesToDeploy;

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

    /**
     * Deploys dummy unit.
     *
     * @param id Deployment unit id.
     * @param workDir Work directory.
     * @param node Node.
     * @throws IOException If failed.
     */
    public static void deployDummyUnit(String id, Path workDir, IgniteServer node) throws IOException {
        Path dummyFile = workDir.resolve("dummy.txt");

        fillDummyFile(dummyFile, 4);

        DeploymentUnit unit = new FilesDeploymentUnit(Map.of(dummyFile.getFileName().toString(), dummyFile));

        NodesToDeploy nodesToDeploy = new NodesToDeploy(InitialDeployMode.MAJORITY);

        IgniteImpl ignite = ((IgniteServerImpl) node).igniteImpl();

        assertThat(ignite.deployment().deployAsync(id, Version.parseVersion("1.0.0"), unit,
                nodesToDeploy), willBe(true));
    }

    /**
     * Generate file with dummy content with provided size.
     *
     * @param file File path.
     * @param fileSize File size in bytes.
     * @throws IOException if an I/O error is thrown.
     */
    public static void fillDummyFile(Path file, long fileSize) throws IOException {
        try (SeekableByteChannel channel = Files.newByteChannel(file, WRITE, CREATE)) {
            channel.position(fileSize - 4);

            ByteBuffer buf = ByteBuffer.allocate(4).putInt(2);
            buf.rewind();
            channel.write(buf);
        }
    }
}
