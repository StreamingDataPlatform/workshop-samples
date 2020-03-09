---
layout: post
category: Processing Data
tags: [stream processing, ingest, flink connector]
subtitle: Code sample for Flink processing
img: flink-connector-pravega.png
author: 
    name: Luis Liu
    description: I'm focusing on the data stream solution development
    image: 
css: 
js: 
---
Battery of code examples to demonstrate the capabilities of Pravega as a data stream storage system for Apache Flink. 
<!--more-->

## Public Repo
[https://github.com/pravega/pravega-samples/tree/master/flink-connector-examples](https://github.com/pravega/pravega-samples/tree/master/flink-connector-examples)

## Pre-requisites
1. Pravega running (see [here](http://pravega.io/docs/latest/getting-started/) for instructions)
2. Build [pravega-samples](https://github.com/pravega/pravega-samples) repository
3. Apache Flink running


## Distributing Flink Samples
Use gradle to assemble a distribution folder containing the Flink programs as a ready-to-deploy 
uber-jar called `pravega-flink-examples-<VERSION>-all.jar`:

```
$ ./gradlew installDist
...
$ ls -R flink-connector-examples/build/install/pravega-flink-examples
bin	lib

flink-connector-examples/build/install/pravega-flink-examples/bin:
run-example

flink-connector-examples/build/install/pravega-flink-examples/lib:
pravega-flink-examples-VERSION-all.jar
```
---

# Examples Catalog

## Word Count

This example demonstrates how to use the Pravega Flink Connectors to write data collected
from an external network stream into a Pravega `Stream` and read the data from the Pravega `Stream`.
See [wordcount]({{site.baseurl}}/processing%20data/2020/03/09/Word-Count-Example-Using-Pravega-Flink-Connectors.html) for more information and execution instructions.


## Exactly Once Sample

This sample demonstrates Pravega EXACTLY_ONCE feature in conjuction with Flink checkpointing and exactly-once mode.
See [Exactly Once Sample]({{site.baseurl}}/processing%20data/2020/03/09/Exactly-Once-Example.html) for instructions.


## StreamCuts Sample

This sample demonstrates the use of Pravega StreamCuts in Flink applications.
See [StreamCuts Sample]({{site.baseurl}}/processing%20data/2020/03/09/StreamCuts-Flink-Example.html) for instructions.

## Pravega Watermark Sample

This sample demonstrates the use of Pravega Watermarks in Flink applications.
See [Watermark Sample]({{site.baseurl}}/processing%20data/2020/03/09/Pravega-Watermark-Flink-Example.html) for instructions.