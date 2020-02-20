package com.dellemc.oe.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.SerializationSchema;

public class JsonSerializationSchema<T> implements SerializationSchema<T> {
    @Override
    public byte[] serialize(T o) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            byte[] result = objectMapper.writeValueAsBytes(o);
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
