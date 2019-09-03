package com.dellemc.oe.readers.util;

import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.descriptors.Schema;

public class EarthQuakeRecord {

    public static TableSchema getTableSchema()
    {
        // Read data from the stream using Table reader
        TableSchema tableSchema = TableSchema.builder()
                .field("DateTime", Types.STRING())
                .field("Latitude", Types.STRING())
                .field("Longitude", Types.STRING())
                .field("Depth", Types.STRING())
                .field("Magnitude", Types.DOUBLE())
                .field("MagType", Types.STRING())
                .field("NbStations", Types.STRING())
                .field("Gap", Types.STRING())
                .field("Distance", Types.STRING())
                .field("RMS", Types.STRING())
                .field("Source", Types.STRING())
                .field("EventID", Types.LONG())
                .build();
        return tableSchema;
    }

    public static Schema getSchema()
    {
        // Read data from the stream using Table reader
        Schema schema = new Schema()
                .field("DateTime", Types.STRING())
                .field("Latitude", Types.STRING())
                .field("Longitude", Types.STRING())
                .field("Depth", Types.STRING())
                .field("Magnitude", Types.DOUBLE())
                .field("MagType", Types.STRING())
                .field("NbStations", Types.STRING())
                .field("Gap", Types.STRING())
                .field("Distance", Types.STRING())
                .field("RMS", Types.STRING())
                .field("Source", Types.STRING())
                .field("EventID", Types.LONG());
        return schema;
    }
}

