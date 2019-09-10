package com.dellemc.oe.readers.util;

import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.table.shaded.org.joda.time.format.DateTimeFormat;
import org.apache.flink.table.shaded.org.joda.time.format.DateTimeFormatter;
import org.apache.flink.types.Row;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Locale;

public class EarthQuakeRecord implements Serializable {

    public static transient DateTimeFormatter TIME_FORMATTER =
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.US).withZoneUTC();

    public static TableSchema getTableSchema()
    {
        // Read data from the stream using Table reader
        TableSchema tableSchema = TableSchema.builder()
                .field("DateTime", Types.STRING())
                .field("Latitude", Types.DOUBLE())
                .field("Longitude", Types.DOUBLE())
                .field("Depth", Types.DOUBLE())
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
                .field("Latitude", Types.DOUBLE())
                .field("Longitude", Types.DOUBLE())
                .field("Depth", Types.DOUBLE())
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

    /*public static Row transform(EarthQuakeRecord earthQuakeRecord) {
        Row row = new Row(getFieldNames().length);
        row.setField(0, tripRecord.getRideId());
        row.setField(1, tripRecord.getVendorId());
        row.setField(2, Timestamp.valueOf(tripRecord.getPickupTime().toString(TIME_FORMATTER)));
        row.setField(3, Timestamp.valueOf(tripRecord.getDropOffTime().toString(TIME_FORMATTER)));
        row.setField(4, tripRecord.getPassengerCount());
        row.setField(5, tripRecord.getTripDistance());
        row.setField(6, tripRecord.getStartLocationId());
        row.setField(7, tripRecord.getDestLocationId());
        row.setField(8, tripRecord.getStartLocationBorough());
        row.setField(9, tripRecord.getStartLocationZone());
        row.setField(10, tripRecord.getStartLocationServiceZone());
        row.setField(11, tripRecord.getDestLocationBorough());
        row.setField(12, tripRecord.getDestLocationZone());
        row.setField(13, tripRecord.getDestLocationServiceZone());
        return row;
    }*/
}

