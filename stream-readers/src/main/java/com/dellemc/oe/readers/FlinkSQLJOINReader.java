/**
 * Copyright (c) Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.dellemc.oe.readers;

import com.dellemc.oe.model.HvacData;
import com.dellemc.oe.model.BuildingData;
import com.dellemc.oe.model.HvacOriginalData;
import com.dellemc.oe.readers.util.HvacRecord;
import com.dellemc.oe.util.AbstractApp;
import com.dellemc.oe.util.AppConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import com.dellemc.oe.serialization.JsonDeserializationSchema;
import io.pravega.connectors.flink.FlinkPravegaReader;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;


import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 *  This flink application demonstrates the joining of two data sets with SQL. To execute this sample run the JSONWriter with data_file HVAC.csv as a input param along with other params.
 */
public class FlinkSQLJOINReader extends AbstractApp {

    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(FlinkSQLJOINReader.class);

    public FlinkSQLJOINReader(AppConfiguration appConfiguration){
        super(appConfiguration);
    }

    public void run() {

        try {
            LOG.info("################## RUN START ################  ");
            // Create Flink Execution environment
            StreamExecutionEnvironment env = initializeFlinkStreaming();
            env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
            // Read table as stream data
            StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

            // create the Pravega input stream (if necessary)
            createStream(appConfiguration.getInputStreamConfig());

            FlinkPravegaReader<HvacRecord> flinkPravegaReader = FlinkPravegaReader.<HvacRecord>builder()
                    .withPravegaConfig(appConfiguration.getPravegaConfig())
                    .forStream(appConfiguration.getInputStreamConfig().getStream())
                    .withDeserializationSchema(new JsonDeserializationSchema(HvacOriginalData.class))
                    .build();

            // Create Table source
            DataStream<HvacRecord> events = env
                    .addSource(flinkPravegaReader)
                    .name("events");
            Table table=tableEnv.fromDataStream(events);

            // Register table resource
            tableEnv.registerTable("hvac", table);
            // calculate and add additional params  diff, temp_range and  extreme_temp
            String sqlQuery = "SELECT DateTime, TargetTemp , ActualTemp,SystemAge,Building_ID ,  (TargetTemp - ActualTemp )  as diff , " +
                    "(CASE WHEN (TargetTemp-ActualTemp)>5 THEN 'HOT' WHEN (TargetTemp-ActualTemp)<-5 THEN 'COLD' ELSE 'NORMAL' END) AS temp_range," +
                    "(CASE WHEN (TargetTemp-ActualTemp)>5 THEN 1 WHEN (TargetTemp-ActualTemp)<-5 THEN 1 ELSE 0 END) AS extreme_temp  from hvac " ;

            Table result = tableEnv.sqlQuery(sqlQuery);
            //  Execute query and get DataStream Object
            DataStream<HvacData>   dataStream = tableEnv.toAppendStream(result, HvacData.class);

            // Print the data
            //dataStream.printToErr();
            //   START seconf source
            // Register as a table in order to run SQL queries against it.
           tableEnv.registerDataStream("hvacStream", dataStream);

            // load the static data from CSV.
            String targetDirString = appConfiguration.getCsvDir();
            File targetDir = new File(targetDirString);
            if (targetDir.exists()){
                LOG.info("################## Target Dir Exists: " +  targetDirString + " ################  ");
            } else {
                if (targetDir.mkdirs()) {
                    LOG.info("################## Target Dir Create: " +  targetDirString + " ################  ");
                } else {
                    LOG.error("################## Cannot Create Target Dir: " +  targetDirString + " ################  ");
                }
            }

            String filePath = targetDirString + "/building.csv";
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream resourceStream = loader.getResourceAsStream("building.csv");

            File file = new File(filePath);
            try(OutputStream outputStream = new FileOutputStream(file)){
                IOUtils.copy(resourceStream, outputStream);
            } catch (FileNotFoundException e) {
                LOG.warn("########## Building File ERROR  #############  " + e.getMessage());
            } catch (IOException e) {
                LOG.error("########## Building File ERROR  #############  " + e.getMessage());
            }

            DataStream<BuildingData> buildingDataStream=env.readTextFile(filePath).map(new MapFunction<String, BuildingData>() {
                @Override
                public BuildingData map(String line) throws Exception {
                    BuildingData buildingData=new BuildingData();
                    return buildingData.getParams(line);
                }
            });
            tableEnv.registerDataStream("building",buildingDataStream);

            // Apply a join  HVAC amd building  and store the results in a dynamic (stream table) table
            Table streamTableJoinResult = tableEnv.sqlQuery("SELECT H.DateTime,  H.TargetTemp ,  H.ActualTemp, H.SystemAge, H.Building_ID, " +
                    "H.diff, H.temp_range, H.extreme_temp, B.BuildingMgr, B.BuildingAge, B.HVACproduct, B.Country " +
                    "FROM hvacStream H  JOIN building B ON H.Building_ID = B.BuildingID");


            // Transform the results again to a stream so that we can print it or use it elsewhere.
            DataStream<Tuple2<Boolean, Row>> finalResult = tableEnv.toRetractStream(streamTableJoinResult, Row.class );

            //  Print final result
            finalResult.printToErr();

            //  END
            // execute within the Flink environment
            env.execute("FlinkSQLJOINReader Reader");

            LOG.info("########## FlinkSQLJOINReader END #############");

        } catch (Exception e) {
            LOG.error("########## HvacSQLReader ERROR  #############  "+e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) throws Exception {
        LOG.info("########## FlinkSQLJOINReader START #############");
        AppConfiguration appConfiguration = new AppConfiguration(args);
        FlinkSQLJOINReader sqlJoinReader = new FlinkSQLJOINReader(appConfiguration);
        sqlJoinReader.run();
    }


}