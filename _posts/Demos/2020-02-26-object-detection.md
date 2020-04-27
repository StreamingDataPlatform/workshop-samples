---
layout: post
category: "Demos"
tags: [pravega, java, ingest, Flink, CUDA, tensorflow]
subtitle: Dell EMC Streaming Data Analytics - Hands-on
img: post/object detection/architecture.png
license: Apache
support: Community
author:
    name: Thejas Vidyasagar
    description: Nautilus app developer.
    image:
css:
js:
---

Hands-on lab to demo how to store, process, read, analyze video on Streaming Data Platform
<!--more-->

## Components
- [Streaming Data Platform](https://www.delltechnologies.com/en-us/storage/streaming-data-platform.htm): Streaming Data Platform allows for harnessing their real-time and historical data in a single, auto-scaling infrastructure and programming model.
- [Pravega](http://pravega.io): Pravega provides a new storage abstraction - a stream - for continuous and unbounded data. A Pravega stream is a durable, elastic, append-only, unbounded sequence of bytes that has good performance and strong consistency. Pravega provides dynamic scaling that can increase and decrease parallelism to automatically respond to changes in the event rate.
- [Flink](https://flink.apache.org): Apache Flink® is an open-source stream processing framework for distributed, high-performing, always-available, and accurate data streaming applications.
- [Docker](https://en.wikipedia.org/wiki/Docker_\(software\)): This demo uses Docker and Docker Compose to greatly simplify the deployment of the various components on Linux and/or Windows servers, desktops, or even laptops.
- GPU: GPUs are essential for increased performance of processing data.
- [CUDA](https://developer.nvidia.com/hpc): To utilize the GPUs, NVIDIA CUDA libraries are required. CUDA 10.0 is used in the project.
- [Tensorflow](https://www.tensorflow.org/): The core open source library to help develop and train ML models.

## Demo environment
![Demo env]({{site.baseurl}}/assets/heliumjk/images/post/object detection/architecture.png)


## Workload Flow
1. Camera recorder collects the video feed into a stream
2. Object Detector job collects the input video stream, and returns output from running on the pre-trained model to detect and identify objects detected into a stream
3. Video Player displays the output video stream with bounding boxes and labels on detected objects

## Source
[https://github.com/pravega/video-samples](https://github.com/pravega/video-samples)