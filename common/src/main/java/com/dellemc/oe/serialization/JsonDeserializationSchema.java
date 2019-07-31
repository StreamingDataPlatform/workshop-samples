package com.dellemc.oe.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.util.serialization.DeserializationSchema;

import java.io.IOException;

public class JsonDeserializationSchema<T> implements DeserializationSchema<T> {
    private Class<T> valueType;

    public JsonDeserializationSchema(Class<T> valueType) {
        this.valueType = valueType;
    }

    @Override
    public T deserialize(byte[] message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(message, valueType);
    }

    @Override
    public boolean isEndOfStream(T nextElement) {
        return false;
    }

    @Override
    public TypeInformation<T> getProducedType() {
        return TypeInformation.of(valueType);
    }
}
