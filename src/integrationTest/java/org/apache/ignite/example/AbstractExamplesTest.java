/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example;

import static org.apache.ignite.example.CompletableFutureMatcher.willCompleteSuccessfully;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.ignite.IgniteServer;
import org.apache.ignite.InitParameters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

/**
 * Base class for creating tests for examples.
 */
public abstract class AbstractExamplesTest {
    private static final String TEST_NODE_NAME = "ignite-node";

    /** Empty argument to invoke an example. */
    protected static final String[] EMPTY_ARGS = new String[0];

    /** Started embedded node. */
    protected IgniteServer node;

    /** GridGain Work directory. */
    @TempDir
    protected Path workDir;

    /**
     * Starts a node.
     */
    @BeforeEach
    public void startNode() throws Exception {
        node = IgniteServer.start(
                TEST_NODE_NAME,
                configFile(),
                workDir
        );

        InitParameters initParameters = InitParameters.builder()
                .metaStorageNodeNames(TEST_NODE_NAME)
                .clusterName("cluster")
                .cmgNodes(node)
                .clusterConfiguration(readGridgainLicense())
                .build();

        assertThat(node.initClusterAsync(initParameters), willCompleteSuccessfully());
        assertThat(node.waitForInitAsync(), willCompleteSuccessfully());
    }

    /**
     * Stops the node.
     */
    @AfterEach
    public void stopNode() {
        node.shutdown();
    }

    /**
     * Copy the original node configuration file to the temporary directory.
     * It needs for the safety reasons: some tests can mutate local configurations (for example storage tests)
     * and mutate this file as a result. So, further tests will run with inappropriate configuration.
     *
     * @return The path of the copied configuration file.
     * @throws IOException If an I/O error occurs during the file copying process.
     */
    private Path configFile() throws IOException {
        var configFileName = "ignite-config.conf";

        return Files.copy(
                Path.of("config", configFileName),
                workDir.resolve(configFileName));
    }

    private String readGridgainLicense() throws IOException {
        var licenseConfigFileName = "gridgain-license.conf";
        var gridgainLicenseEnvPath = System.getenv("GRIDGAIN_LICENSE");
        var licenseFile = gridgainLicenseEnvPath != null
                ? Path.of(gridgainLicenseEnvPath)
                : Path.of("config", licenseConfigFileName);

        if (!Files.isReadable(licenseFile)) {
            throw new IllegalStateException("No license file found.\n"
                    + "Provide a correct license path in $GRIDGAIN_LICENSE environment variable to run tests."
            );
        }

        return Files.readString(licenseFile);
    }
}
