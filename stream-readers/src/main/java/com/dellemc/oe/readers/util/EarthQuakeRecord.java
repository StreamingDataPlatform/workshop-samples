package com.dellemc.oe.readers.util;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.descriptors.Schema;
import java.io.Serializable;

public class EarthQuakeRecord implements Serializable {
    public static TableSchema getTableSchema()
    {
        // Read data from the stream using Table reader
        TableSchema tableSchema = TableSchema.builder()
                .field("DateTime", DataTypes.STRING()) // TODO: Change Date Time fields to Types.SQL_TIMESTAMP()
                .field("Latitude", DataTypes.DOUBLE())
                .field("Longitude", DataTypes.DOUBLE())
                .field("Depth", DataTypes.DOUBLE())
                .field("Magnitude", DataTypes.DOUBLE())
                .field("MagType", DataTypes.STRING())
                .field("NbStations", DataTypes.STRING())
                .field("Gap", DataTypes.STRING())
                .field("Distance", DataTypes.STRING())
                .field("RMS", DataTypes.STRING())
                .field("Source", DataTypes.STRING())
                .field("EventID", DataTypes.BIGINT())
                .build();
        return tableSchema;
    }

    public static Schema getSchema()
    {
        // Read data from the stream using Table reader
        Schema schema = new Schema()
                .field("DateTime", DataTypes.STRING()) // TODO: Change Date Time fields to Types.SQL_TIMESTAMP()
                .field("Latitude", DataTypes.DOUBLE())
                .field("Longitude", DataTypes.DOUBLE())
                .field("Depth", DataTypes.DOUBLE())
                .field("Magnitude", DataTypes.DOUBLE())
                .field("MagType", DataTypes.STRING())
                .field("NbStations", DataTypes.STRING())
                .field("Gap", DataTypes.STRING())
                .field("Distance", DataTypes.STRING())
                .field("RMS", DataTypes.STRING())
                .field("Source", DataTypes.STRING())
                .field("EventID", DataTypes.BIGINT());
        return schema;
    }
}

