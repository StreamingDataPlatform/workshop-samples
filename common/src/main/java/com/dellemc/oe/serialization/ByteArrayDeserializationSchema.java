package com.dellemc.oe.serialization;

import com.dellemc.oe.model.ImageData;
import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;

public class ByteArrayDeserializationSchema extends AbstractDeserializationSchema<ImageData> {

    @Override
    public ImageData deserialize(byte[] message) {
        return new ImageData(message);
    }

}
