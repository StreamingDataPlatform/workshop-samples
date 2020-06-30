---
layout: post
category: Processing Data
tags: [stream processing, ingest, flink connector]
subtitle: WordCountWriter and WordCountReader
img: post/word count example/word-counter.jpg
license: Apache
support: Community
author: 
    name: Luis Liu
    description: I'm focusing on the data stream solution development
    image: 
css: 
js: 
---
This example demonstrates how to use the Pravega Flink Connectors to write data collected
from an external network stream into a Pravega `Stream` and read the data from the Pravega `Stream`.
<!--more-->

## Purpose

Flink provides a DataStream API to perform real-time operations like mapping, windowing, and filtering on continuous unbounded streams of data. Whenever we think about the streaming operations in big data, it has become custom to do the word count. In this example, we will read some lines of words from the [Pravega](http://pravega.io/) data stream storage system and perform the word count on them.  

After complete this word count code sample, you will have a better understanding of how to use the Pravega Flink Connectors to write data collected from an external network stream into a Pravega Stream and read the data from the Pravega Stream.   

Note: This code sample is only designed to run on a local cluster with support from Pravega. It could be an advanced academic exercise to enhance it and run on SDP.  

## Design

This example consists of two applications, a `WordCountWriter` that reads data from a network stream, transforms the data, and writes the data to a Pravega stream; and a
`WordCountReader` that reads from a Pravega stream and prints the word counts summary. You might want to run `WordCountWriter` in one window and `WordCountReader` in another.

## Instructions

#### 1. Setting up the Pravega and Flink environment
Before you start, you need to download the latest Pravega release on the [github releases page](https://github.com/pravega/pravega/releases). See [here](http://pravega.io/docs/latest/getting-started/) for the instructions to build and run Pravega in standalone mode.  

Besides, you also need to get the latest Flink binary from [Apache download page](https://flink.apache.org/downloads.html). Follow [this](https://ci.apache.org/projects/flink/flink-docs-stable/getting-started/tutorials/local_setup.html) tutorial to start a local Flink cluster. 

#### 2. Build `pravega-samples` Repository

`pravega-samples` repository provides code samples to connect analytics engines Flink with Pravega as a storage substrate for data streams. It has divided into sub-projects(`pravega-client-examples`, `flink-connector-examples` and `hadoop-connector-examples`), each one addressed to demonstrate a specific component. To build `pravega-samples` from source, follow the [instructions](https://github.com/pravega/pravega-samples#pravega-samples-build-instructions) to use the built-in gradle wrapper.  

#### 3. Start the Word Count program

There are multiple ways to run the program in Flink environment including submitting from terminal or Flink UI. Follow the latest instruction from the [github README](https://github.com/pravega/pravega-samples/tree/master/flink-connector-examples/doc/flink-wordcount#word-count-example-using-pravega-flink-connectors) to learn more about IDE setup and running process.

## Source
[https://github.com/pravega/pravega-samples/tree/master/flink-connector-examples/doc/flink-wordcount](https://github.com/pravega/pravega-samples/tree/master/flink-connector-examples/doc/flink-wordcount)