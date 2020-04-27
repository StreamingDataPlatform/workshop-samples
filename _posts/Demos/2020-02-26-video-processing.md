---
layout: post
category: "Demos"
tags: [pravega, java, ingest, Flink]
subtitle: Dell EMC Streaming Data Analytics - Hands-on
img: post/video processing/architecture.png
license: Apache
support: Community
author:
    name: Thejas Vidyasagar
    description: Nautilus app developer.
    image:
css:
js:
---

Hands-on lab to demo how to store, process, and read video on Streaming Data Platform
<!--more-->

## Components
- [Streaming Data Platform](https://www.delltechnologies.com/en-us/storage/streaming-data-platform.htm): Streaming Data Platform allows for harnessing their real-time and historical data in a single, auto-scaling infrastructure and programming model. 
- [Pravega](http://pravega.io): Pravega provides a new storage abstraction - a stream - for continuous and unbounded data. A Pravega stream is a durable, elastic, append-only, unbounded sequence of bytes that has good performance and strong consistency. Pravega provides dynamic scaling that can increase and decrease parallelism to automatically respond to changes in the event rate.
- [Flink](https://flink.apache.org): Apache Flink® is an open-source stream processing framework for distributed, high-performing, always-available, and accurate data streaming applications.
- [Docker](https://en.wikipedia.org/wiki/Docker_\(software\)): This demo uses Docker and Docker Compose to greatly simplify the deployment of the various components on Linux and/or Windows servers, desktops, or even laptops.

## Demo environment
![Demo env]({{site.baseurl}}/assets/heliumjk/images/post/video processing/architecture.png)

## Workload Flow
1. Camera recorder collects video feed and sends it into a stream
2. Video Data Generator Job creates randomized data as additional video streams
3. Video Stream, Multi-Video Grid Job and Grid Stream combine the video feeds, resize the images, align by time and join
4. Video Player displays all the video feeds in a grid system

## Source
[https://github.com/pravega/video-samples](https://github.com/pravega/video-samples)