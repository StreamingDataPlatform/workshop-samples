/**
 * Copyright (c) Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.dellemc.oe.flink.wordcount;

import com.dellemc.oe.serialization.JsonSerializationSchema;
import com.dellemc.oe.serialization.UTF8StringDeserializationSchema;
import com.dellemc.oe.util.AbstractApp;
import com.dellemc.oe.util.AppConfiguration;
import io.pravega.client.ClientConfig;
import io.pravega.client.stream.ScalingPolicy;
import io.pravega.client.stream.Stream;
import io.pravega.client.stream.StreamConfiguration;
import io.pravega.connectors.flink.FlinkPravegaReader;
import io.pravega.connectors.flink.FlinkPravegaWriter;
import io.pravega.connectors.flink.PravegaConfig;
import io.pravega.connectors.flink.PravegaEventRouter;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * At a high level, WordCountReader reads from a Pravega stream, and prints
 * the word count summary to the output. This class provides an example for
 * a simple Flink application that reads streaming data from Pravega.
 *
 * And  after flink transformation  output redirect to another pravega stream.
 *
 * This application has the following input parameters
 *     stream - Pravega stream name to read from
 *     controller - the Pravega controller URI, e.g., tcp://localhost:9090
 *                  Note that this parameter is automatically used by the PravegaConfig class
 */
public class WordCountReader extends AbstractApp {

    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(WordCountReader.class);


    // The application reads data from specified Pravega stream and once every 10 seconds
    // prints the distinct words and counts from the previous 10 seconds.
    public WordCountReader(AppConfiguration appConfiguration){
        super(appConfiguration);
    }

    public void run(){
        try {
            AppConfiguration.StreamConfig streamConfig = appConfiguration.getInputStreamConfig();
            //  create stream
            createStream(appConfiguration.getInputStreamConfig());
            // Create EventStreamClientFactory
            ClientConfig clientConfig = appConfiguration.getPravegaConfig().getClientConfig();
            LOG.info("==============  stream  =============== " + streamConfig.getStream().getStreamName());

            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            // create the Pravega source to read a stream of text
            FlinkPravegaReader<String> source = FlinkPravegaReader.<String>builder()
                    .withPravegaConfig(appConfiguration.getPravegaConfig())
                    .forStream(streamConfig.getStream().getStreamName())
                    .withDeserializationSchema(new UTF8StringDeserializationSchema())
                    .build();
            LOG.info("==============  SOURCE  =============== " + source);
            // count each word over a 10 second time period
            DataStream<WordCount> dataStream = env.addSource(source).name(streamConfig.getStream().getStreamName())
                    .flatMap(new WordCountReader.Splitter())
                    .keyBy("word")
                    .timeWindow(Time.seconds(10))
                    .sum("count");

            // create an output sink to print to stdout for verification
            dataStream.printToErr();

            LOG.info("==============  PRINTED  ===============");
            AppConfiguration.StreamConfig outputStreamConfig = appConfiguration.getOutputStreamConfig();
            //  create stream
            createStream(appConfiguration.getOutputStreamConfig());
            FlinkPravegaWriter<WordCount> writer = FlinkPravegaWriter.<WordCount>builder()
                    .withPravegaConfig(appConfiguration.getPravegaConfig())
                    .forStream(outputStreamConfig.getStream().getStreamName())
                    .withEventRouter(new EventRouter())
                    .withSerializationSchema(new JsonSerializationSchema())
                    .build();
            dataStream.addSink(writer).name("OutputStream");

            // create another output sink to print to stdout for verification

            LOG.info("============== Final output ===============");
            dataStream.printToErr();
            // execute within the Flink environment
            env.execute("WordCountReader");

            LOG.info("Ending WordCountReader...");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        LOG.info("Starting WordCountReader...");
        AppConfiguration appConfiguration = new AppConfiguration(args);
        WordCountReader wordCounter = new WordCountReader(appConfiguration);
        wordCounter.run();
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

    // split data into word by space
    private static class Splitter implements FlatMapFunction<String, WordCount> {
        @Override
        public void flatMap(String line, Collector<WordCount> out) throws Exception {
            for (String word : line.split(" ")) {
                out.collect(new WordCount(word, 1));
            }
        }
    }

}
