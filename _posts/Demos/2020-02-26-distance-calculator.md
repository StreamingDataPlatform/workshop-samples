---
layout: post
category: Demos
tags: [pravega, flink, gateway, mqtt]
subtitle: Dell EMC Streaming Data Analytics - Hands-on
img: post/distance calculator/architecture.png
license: Apache
support: Community
author: 
    name: Luis Liu
    description: I am a nautilus app developer.
    image:
css: 
js: 
---

This post demos how to make a distance calculator on Streaming Data Platform
<!--more-->

## Introduction

- Local deployment of InfluxDB+Grafana+Pravega+IoT GW 
- Optional: Connect Raspberry Pi 4 and run the app to collect the data in json format via a distance sensor
- Using local IDE (Or local Flink cluster) to run Flink Distance Calculation Job
    - Fixed window
        > Calculate the average value of each fixed time window (3s)
    - Distance Calculation logical
        > Normal: <=1m
        > A little Far: > 1m && <= 3m
        > Far: > 3m

### Demo environment
![Demo env]({{site.baseurl}}/assets/heliumjk/images/post/distance calculator/architecture.png)

### Workload Flow
1. Distance sensor collects the data via Raspberry Pi and write into one MQTT topic or Use simulator to generate the data from one CSV file
2. Consume the injected data into MQTT topic
3. Write it into the Pravega Stream via Pravega Writr API
4. Flink Job read the data from stream using Pravega Reader API
5. Computing and processing in each fixed 3 time window
6. Sink to Influx DB
![Workflow]({{site.baseurl}}/assets/heliumjk/images/post/distance calculator/workflow.png)

### Tools
- [MobaXterm](https://mobaxterm.mobatek.net/download.html)
- [FireFox](https://www.mozilla.org/en-US/firefox/new/)
- [IntelliJ IDEA Community](https://www.jetbrains.com/idea/download/#section=windows)
- [Java SDK 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

### Runtime Preparation
- [Open Java 8](http://openjdk.java.net/install/)
- [Docker CE](https://docs.docker.com/install/linux/docker-ce/ubuntu/)
- [Docker Registry server](https://docs.docker.com/registry/deploying/)
- [Docker Compose](https://docs.docker.com/compose/install/)

### Set up IDE
1. Select Project -> File -> settings -> plugins and install Lombok plugin
2. Select Project -> File -> settings -> Build,Execution,Deployment -> Annotation Process 
![setup1]({{site.baseurl}}/assets/heliumjk/images/post/distance calculator/setup1.png)

3. Select Project -> File -> settings -> Build Tools -> Gradle 
![setup2]({{site.baseurl}}/assets/heliumjk/images/post/distance calculator/setup2.png)


### Configure docker-compose.yml
specify the following network related parameters
- Pravega
    - environment:
        - HOST_IP: 192.168.188.130
- mqttwriter:
    - environment:
        - MQTT_BROKER_URL: tcp://192.168.188.130:1883
- pravegagw:
    - environment:
        - PRAVEGA_CONTROLLER: tcp://192.168.188.130:9090
        - MQTT_BROKER_URL: tcp://192.168.188.130:1883

### Local deployment of InfluxDB+Grafana+Pravega+IoT GW 
1. Download the code from github
2. Go to workdir
```
cd /home/ubuntu/piedpiper/DistanceCalculation/
```
3. Deploy
```
docker-compose up
``` 
Or (running in the background)
```
docker-compose up –d
``` 
4. Check the status
```
docker-compose ps
```
5. Un-deploy
```
docker-compose down
```

### Using local IDE to run Flink job
1. Using IntelliJ IDEA to open the "Flink Distance Calculation" project
![job1]({{site.baseurl}}/assets/heliumjk/images/post/distance calculator/job1.png)

2. New a task
![job2]({{site.baseurl}}/assets/heliumjk/images/post/distance calculator/job2.png)
![job3]({{site.baseurl}}/assets/heliumjk/images/post/distance calculator/job3.png)
![job4]({{site.baseurl}}/assets/heliumjk/images/post/distance calculator/job4.png)
![job5]({{site.baseurl}}/assets/heliumjk/images/post/distance calculator/job5.png)
![job6]({{site.baseurl}}/assets/heliumjk/images/post/distance calculator/job6.png)
![job7]({{site.baseurl}}/assets/heliumjk/images/post/distance calculator/job7.png)

3. Run that task
![job8]({{site.baseurl}}/assets/heliumjk/images/post/distance calculator/job8.png)

### Visualization on Grafana
1. Login to (http://[Host IP]:3000), Open Grafana dashboard(Username/Password: admin/password), select the dashboard "Distance"
2. Optional：modify the time range and refresh sequence if using the simulator to generate the data or directly access http://[Host IP]:3000/d/7jAI6_-Wk/distance?orgId=1&from=1581820530000&to=1581821760000&refresh=5s

Screenshot of Grafana
![job9]({{site.baseurl}}/assets/heliumjk/images/post/distance calculator/job9.png)

### Deploy a local Fink cluster
1. Download the deployment files
```
git clone https://github.com/docker-flink/examples.git
cd ./examples
```

2. Deploy Flink cluster 
```
docker-compose up 
```

3. TaskManagers scale in/out
```
docker-compose scale taskmanager=<N> 
```

4. undeploy the cluster
```
docker-compose kill/docker-compose down
```

### Package Flink Job App 
1. Performing the "jar" task
![flink1]({{site.baseurl}}/assets/heliumjk/images/post/distance calculator/flink1.png)

2. After the task run successfully, gain the "distance-calculator-4.0.jar" in this project workDIR 
"DistanceCalculator\distance-calculator\build\libs"
![flink2]({{site.baseurl}}/assets/heliumjk/images/post/distance calculator/flink2.png)

### Upload Flink App into Flink cluster
1. Login to Flink http://[Host IP]:8081
![flink3]({{site.baseurl}}/assets/heliumjk/images/post/distance calculator/flink3.png)

2. Submit Flink Job
![flink4]({{site.baseurl}}/assets/heliumjk/images/post/distance calculator/flink4.png)

## Source
[https://github.com/vangork/sdp-starter-kit/tree/master/distance-calculator](https://github.com/vangork/sdp-starter-kit/tree/master/distance-calculator)