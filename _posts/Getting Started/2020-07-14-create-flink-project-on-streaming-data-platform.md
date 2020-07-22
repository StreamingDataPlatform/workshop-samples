---
layout: post
category: "Getting Started"
tags: [flink, java, SDP, Pravega]
subtitle: "Create Projects and Flink Clusters on Dell EMC Streaming Data Platform"
img: sdp.jpg
license: Apache
support: Community
author: 
    name: Youmin Han
    description: Nautilus App Developer
    image: batman.png
css: 
js: 
---
This is a general guide for creating and setting up Flink job on Dell EMC Streaming Data Platform.
<!--more-->

## Purpose
Apache Flink is the embedded analytics engine in the Dell EMC Streaming Data Platform which provides a seamless way for development teams to deploy analytics applications. This post describes the general instruction of using Apache Flink<sup>®</sup> applications with the Streaming Data Platform.

## Instructions
##### A. Remove `createScope` method from the code
**1.** If you have used `createScope` method in Pravega standalone mode. Please **comment out** from your code since the SDP does not allow to create a scope from the code.

##### B. Create Projects
**1.** Log in to the Dell EMC Streaming Data Platform and click the **Analytics** icon to navigate to the Analytics Projects view which lists the projects you have access to. 
![Analytics-Project]({{site.baseurl}}/assets/heliumjk/images/post/flink-sdp-setup/analytics-project.png)
**Note:** If you cannot log in to the Dell EMC Streaming Data Platform, please use ```kubectl get ingress -A``` to find all ingress and add to your **/etc/hosts** file.  

**2.** Click **Create Project** button to start with a new project by typing the project name, description and required volume size. This will also automatically create a scope or namespace for your project in Pravega. 
![Create-Project]({{site.baseurl}}/assets/heliumjk/images/post/flink-sdp-setup/create-project.png)

##### C. Create Flink Clusters
**1.** To create a Flink cluster using the Dell EMC Streaming Data Platform UI, navigate to **Analytics -> *project-name* > Flink Clusters**.  
![Flink-Cluster]({{site.baseurl}}/assets/heliumjk/images/post/flink-sdp-setup/flink-cluster.png)

**2.** Click **Create Flink Cluster** and complete the cluster configuration fields.
![Create-Flink-Cluster]({{site.baseurl}}/assets/heliumjk/images/post/flink-sdp-setup/create-flink-cluster.png)

##### D. Upload aritifact to SDP
###### Option I: Using the Dell EMC Streaming Data Platform UI
**1.** Navigate to **Analytics > Analytics Projects > *project-name* > Artifacts**
![Artifact-UI]({{site.baseurl}}/assets/heliumjk/images/post/flink-sdp-setup/artifact-ui.png)

**2.** Click **Upload Artifact**. Under the **General** section, specify the **Maven Group**, the **Maven Artifact**, and enter the **Version number**. Under the **Artifact** File section, browse to the JAR file on your local machine and select the file to upload to the repository. 
![Upload-Artifact]({{site.baseurl}}/assets/heliumjk/images/post/flink-sdp-setup/upload-artifact.png)

###### Option II: Using the Gradle Build Tool
**1.** Make the Maven repo in SDP available to your development workstation.
```
kubectl port-forward service/repo 9090:80 --namespace project-name
```

**2.** Add a publishing section to your `build.gradle` file. The standard `maven-publish` Gradle plugin must be added to the project in addition to a credentials section. You may also need to customize the publication identity, such as **groupId**, **artifactId**, and **version**. If you publish shadow JARs, make sure add `classifier = ""` and `zip64 true` to the `shadowJar` config. The following is an example `build.gradle` file.
```
apply plugin: "maven-publish"
publishing {
    repositories {
        maven {
            url = "http://localhost:9090/maven2"
            credentials {
            username "desdp"
            password "password"
            }
            authentication {
                basic(BasicAuthentication)
            }
        }
    }

    publications {
        maven(MavenPublication) {
            groupId = 'com.dell.com'
            artifactId = 'anomaly-detection'
            version = '1.0'
            from components.java
        }
    }
}
```

**3.** Use the Gradle Extension either from  IntelliJ IDEA or [Command-Line Interface](https://docs.gradle.org/current/userguide/command_line_interface.html) to publish the application JAR file to SDP.

##### E. Deploy Applications
**1.** Navigate to **Analytics > Analytics Projects > *project-name* > Apps**
![App-UI]({{site.baseurl}}/assets/heliumjk/images/post/flink-sdp-setup/app.png)

**2.**  Click **Create New App**. Specify the application name, the artifact source, main class, and other configuration information. You also have the chance to create a new Pravega stream for your application.
![Create-App]({{site.baseurl}}/assets/heliumjk/images/post/flink-sdp-setup/create-app.png)

##### F. Check Project Status
**1.** Navigate to **Analytics > Analytics Projects > *project-name***. Use the links at the top of the project dashboard to view Apache Flink clusters, applications, and artifacts associated with the project.
![Dashboard-UI]({{site.baseurl}}/assets/heliumjk/images/post/flink-sdp-setup/dashboard.png)


## Source
[Dell EMC Streaming Data Platform InfoHub](https://www.dell.com/support/article/en-us/sln319974/dell-emc-streaming-data-platform-infohub?lang=en)

## Documentation
For the detailed description for each attribute on the configuration screen, please refer to the  [Dell EMC Streaming Data Platform Developer's Guide](https://www.dellemc.com/en-us/collaterals/unauth/technical-guides-support-information/2020/01/docu96951.pdf).
