---
layout: post
category: Connectors
tags: [pravega, Nifi, connector]
subtitle: Enable Apache Nifi to read and write Pravega streams
img: nifilogo.jfif
license: Apache
support: Community
author: 
    name: Venkatasubbaiah Chenna
    description: SDP App Developer
    image: 
css: 
js: 
---
This post introduces connectors to read and write [Pravega](http://pravega.io/) Streams with [Apache Nifi](https://nifi.apache.org/) stream processing framework.
<!--more-->

The connectors can be used to build end-to-end stream processing pipelines (see [Samples](https://github.com/pravega/nifi-pravega))  that use Pravega as the stream storage and message bus, and Apache Flink for computation over the streams.


## Features & Highlights

**PublishPravega:** This processor writes incoming FlowFiles to a Pravega stream. It uses Pravega transactions to provide at-least-one guarantees.

**PublishPravegaRecord:** This is similar to PublishPravega but it uses a NiFi Record Reader to parse the incoming FlowFiles as CSV, JSON, or Avro. Each record will be written as a separate event to a Pravega stream. Events written to Pravega will be serialized with a Record Writer and can be CSV, JSON, or Avro.

**ConsumePravega:** This processor reads events from a Pravega stream and produces FlowFiles. This processor stores the most recent successful Pravega checkpoint in the NiFi cluster state to allow it to resume when restarting the processor or node. It provides at-least-once guarantees.

All of these processors allow for concurrency in a NiFi cluster or a standalone NiFi node.

## Source
[https://github.com/pravega/nifi-pravega](https://github.com/pravega/nifi-pravega)

## Documentation
To learn more about how to build and use the Flink Connector library, follow the connector documentation [here](http://pravega.io/).

More examples on how to use the connectors with Flink application can be found in [Pravega Samples](https://github.com/pravega/nifi-pravega) repository.

## License
Flink connectors for Pravega is 100% open source and community-driven. All components are available
under [Apache 2 License](https://www.apache.org/licenses/LICENSE-2.0.html) on GitHub.