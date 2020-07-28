---
layout: post
category: "Getting Started"
tags: [pravega, java, pravega-client]
subtitle: "Pravega Client Examples"
img: pravega.png
technologies: [Pravega]
license: Apache
support: Community
author: 
    name: Pravega Team
    description: 
    image: pravega-bird.jpg
css: 
js: 
---
These examples show how to initialize and use the Pravega Client to write and read data to/from a Pravega Stream in Java.

### Instructions

1. Pravega must be running (see [here](http://pravega.io/docs/latest/getting-started/) for instructions)
1. Clone the [pravega-samples](https://github.com/pravega/pravega-samples) repository
1. Build pravega-samples ([details](https://github.com/pravega/pravega-samples#pravega-samples-build-instructions))

Please note that after building pravega-samples, all the executables used here are located in:
```
pravega-samples/pravega-client-examples/build/install/pravega-client-examples
```

### Examples included

* [gettingstarted](https://github.com/pravega/pravega-samples/tree/master/pravega-client-examples#gettingstarted)
    * A HelloWorld example of reading and writing with the client
* [consolerw](https://github.com/pravega/pravega-samples/tree/master/pravega-client-examples#consolerw)
    * A console reader and writer pair
* [noop](https://github.com/pravega/pravega-samples/tree/master/pravega-client-examples#noop)
    * Simple reader that outputs stream statistics
* [statesynchronizer](https://github.com/pravega/pravega-samples/tree/master/pravega-client-examples#statesynchronizer)
    * Illustrates how to make use of Pravega's state synchronizer to share state in a distributed application
* [streamcuts](https://github.com/pravega/pravega-samples/tree/master/pravega-client-examples#streamcuts)
    * Shows how to use streamcuts (identifying specific points in the stream)
* [secure](https://github.com/pravega/pravega-samples/tree/master/pravega-client-examples#secure)
    * Shows how to use TLS and credentials

### Source
[https://github.com/pravega/pravega-samples/tree/master/pravega-client-examples](https://github.com/pravega/pravega-samples/tree/master/pravega-client-examples)

### Documentation
[https://github.com/pravega/pravega-samples/tree/master/pravega-client-examples](https://github.com/pravega/pravega-samples/tree/master/pravega-client-examples)
