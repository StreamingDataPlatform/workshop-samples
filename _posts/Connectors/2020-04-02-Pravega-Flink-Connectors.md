---
layout: post
category: Connectors
tags: [pravega, flink, connector]
subtitle: Enable Flink to read and write Pravega streams
img: flink.jfif
license: Apache
support: Community
author: 
    name: Luis Liu
    description: SDP App Developer
    image: 
css: 
js: 
---
This post introduces connectors to read and write [Pravega](http://pravega.io/) Streams with [Apache Flink](http://flink.apache.org/) stream processing framework.
<!--more-->

The connectors can be used to build end-to-end stream processing pipelines (see [Samples](https://github.com/pravega/pravega-samples))  that use Pravega as the stream storage and message bus, and Apache Flink for computation over the streams.


## Features & Highlights

  - **Exactly-once processing guarantees** for both Reader and Writer, supporting **end-to-end exactly-once processing pipelines**

  - Seamless integration with Flink's checkpoints and savepoints.

  - Parallel Readers and Writers supporting high throughput and low latency processing.

  - Table API support to access Pravega Streams for both **Batch** and **Streaming** use case.

## Source
[https://github.com/pravega/flink-connectors](https://github.com/pravega/flink-connectors)

## Documentation
To learn more about how to build and use the Flink Connector library, follow the connector documentation [here](http://pravega.io/).

More examples on how to use the connectors with Flink application can be found in [Pravega Samples](https://github.com/pravega/pravega-samples) repository.

## License
Flink connectors for Pravega is 100% open source and community-driven. All components are available
under [Apache 2 License](https://www.apache.org/licenses/LICENSE-2.0.html) on GitHub.