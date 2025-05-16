# GridGain 9 Examples

## Overview

This project contains code examples for GridGain 9.

Examples are shipped as a Gradle module, so to start running you simply need
to import the provided `build.gradle` file into your favourite IDE.

To run tests you need to provide a valid gridgain licence.
To do that edit the file `$HOME/gradle.properties` adding the following content:

```properties
gridgain_license=<PATH_TO/gridgain_license.conf>
```

The following examples are included:
* `KeyValueViewDataStreamerExample` - demonstrates the usage of the `DataStreamerTarget#streamData(Publisher, DataStreamerOptions)` API
  with the `KeyValueView`.
* `KeyValueViewPojoDataStreamerExample` - demonstrates the usage of the `DataStreamerTarget#streamData(Publisher, DataStreamerOptions)` API
  with the `KeyValueView` and user-defined POJOs.
* `RecordViewDataStreamerExample` - demonstrates the usage of the `DataStreamerTarget#streamData(Publisher, DataStreamerOptions)` API
  with the `RecordView`.
* `RecordViewPojoDataStreamerExample` - demonstrates the usage of the `DataStreamerTarget#streamData(Publisher, DataStreamerOptions)` API
  with the `RecordView` and user-defined POJOs.
* `ReceiverStreamProcessingExample` - demonstrates the usage of
  the `DataStreamerTarget#streamData(Publisher, Function, Function, ReceiverDescriptor, Subscriber, DataStreamerOptions, Object)` API
  for stream processing of the trades data read from the file.
* `ReceiverStreamProcessingWithResultSubscriberExample` - demonstrates the usage of
  the `DataStreamerTarget#streamData(Publisher, Function, Function, ReceiverDescriptor, Subscriber, DataStreamerOptions, Object)` API
  for stream processing of the trade data and receiving processing results.
* `ReceiverStreamProcessingWithTableUpdateExample` - demonstrates the usage of
  the `DataStreamerTarget#streamData(Publisher, Function, Function, ReceiverDescriptor, Subscriber, DataStreamerOptions, Object)` API
  for stream processing of the trade data and updating account data in the table.
* `ContinuousQueryExample` - demonstrates the usage of the `ContinuousQuerySource` API.
* `ContinuousQueryTransactionsExample` - demonstrates the usage of the `ContinuousQuerySource` API with transactions.
* `ContinuousQueryResumeFromWatermarkExample` - demonstrates the usage of the `ContinuousQuerySource` API resuming from a specific
  watermark.
* `ComputeAsyncExample` - demonstrates the usage of the `IgniteCompute#executeAsync(JobTarget, JobDescriptor, Object)` API.
* `ComputeBroadcastExample` - demonstrates the usage of the `IgniteCompute#execute(BroadcastJobTarget, JobDescriptor, Object)` API.
* `ComputeCancellationExample` - demonstrates the usage of
  the `IgniteCompute#executeAsync(JobTarget, JobDescriptor, Object, CancellationToken)` API.
* `ComputeColocatedExample` - demonstrates the usage of
  the `IgniteCompute#execute(JobTarget, JobDescriptor, Object)` API with colocated JobTarget.
* `ComputeExample` - demonstrates the usage of the `IgniteCompute#execute(JobTarget, JobDescriptor, Object)` API.
* `ComputeJobPriorityExample` - demonstrates the usage of
  the `IgniteCompute#execute(JobTarget, JobDescriptor, Object)` API with different job priorities.
* `ComputeMapReduceExample` - demonstrates the usage of the `IgniteCompute#executeMapReduce(TaskDescriptor, Object)` API.
* `ComputeWithCustomResultMarshallerExample` - demonstrates the usage of the `IgniteCompute#execute(JobTarget, JobDescriptor, Object)` API
  with a custom result marshaller.
* `ComputeWithResultExample` - demonstrates the usage of the `IgniteCompute#execute(JobTarget, JobDescriptor, Object)`}` API
  with a result return.
* `SqlJdbcCopyExample` - demonstrates the usage of the `COPY` command via JDBC driver.

Then, to run tests, invoke:
```shell
./gradlew :integrationTest
```

## Running examples with a GridGain node within a Docker container

1. Start a GridGain node:
```shell
docker run --name gridgain9-node -d --rm -p 10300:10300 -p 10800:10800 \
  -v $(pwd)/config/ignite-config.conf:/opt/ignite/etc/ignite-config.conf gridgain/gridgain9
```

2. Get the IP address of the node:
```shell
NODE_IP_ADDRESS=$(docker inspect --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' gridgain9-node)
```

3. Initialize the node:
```shell
docker run -v <PATH_TO_GRIDGAIN_LICENSE_FILE>:/opt/gridgain9cli/license.conf gridgain/gridgain9 \
  cli cluster init --url http://$NODE_IP_ADDRESS:10300 --name myCluster1 --metastorage-group defaultNode \
  --config-files=/opt/gridgain9cli/license.conf
```
4. Run the example via IDE.

5. Stop the GridGain node:
```shell
docker stop gridgain9-node
```

## Running examples with a GridGain node started natively

1. Download the GridGain ZIP package including the database and CLI parts. Unpack.

2. Prepare the environment variables. `GRIDGAIN_HOME` is used in the GridGain startup. Therefore, you need to export it:
```shell
export GRIDGAIN_HOME=/path/to/gridgain9-db-dir
export GRIDGAIN_CLI_HOME=/path/to/gridgain9-cli-dir
```

3. Override the default configuration file:
```shell
echo "CONFIG_FILE=config/ignite-config.conf" >> $GRIDGAIN_HOME/etc/vars.env
```

4. Start an GridGain node using the startup script from the database part:
```shell
$GRIDGAIN_HOME/bin/gridgain9db
```

5. Initialize the cluster using GridGain CLI from the CLI part:
```shell
$GRIDGAIN_CLI_HOME/bin/gridgain9 cluster init --name myCluster1 --metastorage-group defaultNode --config-files=<PATH_TO_GRIDGAIN_LICENSE_FILE>
```

6. Run the example from the IDE.

7. Stop the GridGain node by stopping the gridgain9db process. 
See additional details here: https://www.gridgain.com/docs/gridgain9/latest/quick-start/getting-started-guide#stop-the-node
