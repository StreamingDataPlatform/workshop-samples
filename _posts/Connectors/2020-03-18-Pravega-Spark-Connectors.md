---
layout: post
category: Connectors
tags: [pravega, spark, connector]
subtitle: Enable Spark to read and write Pravega streams
img: spark.png
license: Apache
support: Community
author: 
    name: Luis Liu
    description: SDP App Developer
    image: 
css: 
js: 
---
This post introduces the Pravega Spark connectors that read and write [Pravega](http://pravega.io/) Streams with [Apache Spark](http://spark.apache.org/), a high-performance analytics engine for batch and streaming data.
<!--more-->

The connectors can be used to build end-to-end stream processing pipelines (see [Samples](https://github.com/pravega/pravega-samples)) that use Pravega as the stream storage and message bus, and Apache Spark for computation over the streams.



## Features & Highlights

  - **Exactly-once processing guarantees** for both Reader and Writer, supporting **end-to-end exactly-once processing pipelines**.

  - A Spark V2 data source micro-batch reader connector allows Spark Streaming applications to read Pravega Streams.
    Pravega stream cuts are used to reliably recover from failures and provide exactly-once semantics.
    
  - A Spark base relation data source batch reader connector allows Spark batch applications to read Pravega Streams.

  - A Spark V2 data source stream writer allows Spark Streaming applications to write to Pravega Streams.
    Writes are contained within Pravega transactions, providing exactly-once semantics.

  - Seamless integration with Spark's checkpoints.

  - Parallel Readers and Writers supporting high throughput and low latency processing.

  - Reader supports reassembling chunked events to support events of 2 GiB.

## Limitations

  - The current implementation of this connector does *not* guarantee that events with the same routing key
    are returned in a single partition. 
    If your application requires this, you must repartition the dataframe by the routing key and sort within the
    partition by segment_id and offset.

  - Continuous reader support is not available. The micro-batch reader uses the Pravega batch API and works well for
    applications with latency requirements above 100 milliseconds.

  - The initial batch in the micro-batch reader will contain the entire Pravega stream as of the start time.
    There is no rate limiting functionality.

  - Read-after-write consistency is currently *not* guaranteed.
    Be cautious if your workflow requires multiple chained Spark batch jobs.

## Build and Install the Spark Connector

This will build the Spark Connector and publish it to your local Maven repository.

```
cd
git clone https://github.com/pravega/spark-connectors
cd spark-connectors
./gradlew install
ls -lhR ~/.m2/repository/io/pravega/pravega-connectors-spark
```

## Source
[https://github.com/pravega/spark-connectors](https://github.com/pravega/spark-connectors)

## Documentation

To learn more about how to build and use the Spark Connector library, refer to
[Pravega Samples](https://github.com/claudiofahey/pravega-samples/tree/spark-connector-examples/spark-connector-examples).

## Reference
[http://blog.madhukaraphatak.com/spark-datasource-v2-part-1/](http://blog.madhukaraphatak.com/spark-datasource-v2-part-1/)

## License

Spark connectors for Pravega is 100% open source and community-driven. All components are available
under [Apache 2 License](https://www.apache.org/licenses/LICENSE-2.0.html) on GitHub.