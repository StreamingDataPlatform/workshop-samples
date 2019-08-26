package com.dellemc.oe.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple data generator for  generating CSV data to JSON and  the data will be used in samples.
 */
public class DataGenerator {
    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(DataGenerator.class);

    public static void main(String[] args) throws Exception {
        //csvtojson() ;
        //convert();
        convertCsvToJson();
    }

    /**
     *  Read CSV file and generate  JSON  String as json array of all rows.
     */
    public static String convertCsvToJson() throws Exception {
        File csvFile =  new DataGenerator().getFileFromResources("earthquake-database.csv");
        File output = new File("c:/tmp/output.json");

        CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
        CsvMapper csvMapper = new CsvMapper();

        // Read data from CSV file
        List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(csvFile).readAll();

        ObjectMapper mapper = new ObjectMapper();

        // Write JSON formated data to output.json file
        //mapper.writerWithDefaultPrettyPrinter().writeValue(output, readAll);

        // Write JSON formated data to stdout
        String result = mapper.writeValueAsString(readAll);

        LOG.debug("@@@@@@@@@@@@@ DATA  @@@@@@@@@@@@@  "+result);

        return result;
    }

    /**
     *  Read CSV file and generate  JSON  file.
     */
    public static void  covertCSVToJsonAndSaveAsFile() throws Exception {

        JsonFactory fac = new JsonFactory();
        Pattern pattern = Pattern.compile(",");
        File csvFile =  new DataGenerator().getFileFromResources("earthquake-database.csv");
        String jsonFile = "/tmp/earthquake-database.json";
        try (BufferedReader in = new BufferedReader(new FileReader(csvFile));
             JsonGenerator gen = fac.createGenerator(new File(jsonFile),
                     JsonEncoding.UTF8);) {
            String[] headers = pattern.split(in.readLine());
            gen.writeStartArray();
            String line;
            while ((line = in.readLine()) != null) {
                gen.writeStartObject();
                String[] values = pattern.split(line);
                for (int i = 0 ; i < headers.length ; i++) {
                    String value = i < values.length ? values[i] : null;
                    gen.writeStringField(headers[i], value);
                }
                gen.writeEndObject();
            }
            gen.writeEndArray();
        }
    }

    // get file from classpath, resources folder
    private File getFileFromResources(String fileName) {

        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }

    }
}