/*
 * Copyright (c) 2018 Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 */
package com.dellemc.oe.flink.wordcount;

import com.dellemc.oe.util.CommonParams;

import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.ScalingPolicy;
import io.pravega.client.stream.Stream;
import io.pravega.client.stream.StreamConfiguration;
import io.pravega.connectors.flink.FlinkPravegaReader;
import io.pravega.connectors.flink.FlinkPravegaWriter;
import io.pravega.connectors.flink.PravegaConfig;
import io.pravega.connectors.flink.PravegaEventRouter;
import io.pravega.connectors.flink.serialization.PravegaSerialization;
import com.dellemc.oe.flink.Utils;
import com.dellemc.oe.util.CommonParams;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/*
 * At a high level, WordCountReader reads from a Pravega stream, and prints 
 * the word count summary to the output. This class provides an example for 
 * a simple Flink application that reads streaming data from Pravega.
 *
 * This application has the following input parameters
 *     stream - Pravega stream name to read from
 *     controller - the Pravega controller URI, e.g., tcp://localhost:9090
 *                  Note that this parameter is automatically used by the PravegaConfig class
 */
public class WordCountReader {

    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(WordCountReader.class);

    // The application reads data from specified Pravega stream and once every 10 seconds
    // prints the distinct words and counts from the previous 10 seconds.

    // Application parameters
    //   stream - default examples/wordcount
    //   controller - default tcp://127.0.0.1:9090

    public static void main(String[] args) throws Exception {
        LOG.info("Starting WordCountReader...");

        final String scope = CommonParams.getScope();
        final String streamName = CommonParams.getStreamName();
        final URI controllerURI = CommonParams.getControllerURI();


        // initialize the parameter utility tool in order to retrieve input parameters
         PravegaConfig pravegaConfig = PravegaConfig.fromDefaults()
                .withControllerURI(controllerURI)
                .withDefaultScope(scope)
                //.withCredentials(credentials)
                .withHostnameValidation(false);
        System.out.println("==============  pravegaConfig  =============== "+pravegaConfig);
        // create the Pravega input stream (if necessary)
        Stream stream = Utils.createStream(
                pravegaConfig,
                streamName);
        System.out.println("==============  stream  =============== "+stream);

        // initialize the Flink execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // create the Pravega source to read a stream of text
        FlinkPravegaReader<String> source = FlinkPravegaReader.<String>builder()
                .withPravegaConfig(pravegaConfig)
                .forStream(stream)
                .withDeserializationSchema(PravegaSerialization.deserializationFor(String.class))
                .build();
        System.out.println("==============  SOURCE  ===============");
        // count each word over a 10 second time period
        DataStream<WordCount> dataStream = env.addSource(source).name(streamName)
                .flatMap(new WordCountReader.Splitter())
                .keyBy("word")
                .timeWindow(Time.seconds(10))
                .sum("count");

        // create an output sink to print to stdout for verification
        dataStream.print();
        System.out.println("==============  PRINTED  ===============");
        Stream output_stream = getOrCreateStream(pravegaConfig, "output-stream", 3);
        // create the Pravega sink to write a stream of text
        FlinkPravegaWriter<WordCount> writer = FlinkPravegaWriter.<WordCount>builder()
                .withPravegaConfig(pravegaConfig)
                .forStream(output_stream)
                .withEventRouter(new  EventRouter())
                .withSerializationSchema(PravegaSerialization.serializationFor(WordCount.class))
                .build();
        dataStream.addSink(writer).name("OutputStream");

        // create another output sink to print to stdout for verification
        //avgTemp.print().name("stdout");
        System.out.println("============== Final output ===============");
        dataStream.print();


        // execute within the Flink environment
        env.execute("WordCountReader");

        LOG.info("Ending WordCountReader...");
    }

    /*
     * Event Router class
     */
    public static class EventRouter implements PravegaEventRouter<WordCount> {
        // Ordering - events with the same routing key will always be
        // read in the order they were written
        @Override
        public String getRoutingKey(WordCount event) {
            return "SameRoutingKey";
        }
    }

    static public Stream getOrCreateStream(PravegaConfig pravegaConfig, String streamName, int numSegments) {
        StreamConfiguration streamConfig = StreamConfiguration.builder()
                .scalingPolicy(ScalingPolicy.fixed(numSegments))
                .build();

        return  createStream(pravegaConfig, streamName, streamConfig);
    }

    static Stream createStream(PravegaConfig pravegaConfig, String streamName, StreamConfiguration streamConfig) {
        // resolve the qualified name of the stream
        Stream stream = pravegaConfig.resolve(streamName);

        try(StreamManager streamManager = StreamManager.create(pravegaConfig.getClientConfig())) {
            // create the requested stream based on the given stream configuration
            streamManager.createStream(stream.getScope(), stream.getStreamName(), streamConfig);
        }

        return stream;
    }

    // split data into word by space
    private static class Splitter implements FlatMapFunction<String, WordCount> {
        @Override
        public void flatMap(String line, Collector<WordCount> out) throws Exception {
            for (String word: line.split(Constants.WORD_SEPARATOR)) {
                out.collect(new WordCount(word, 1));
            }
        }
    }

}
