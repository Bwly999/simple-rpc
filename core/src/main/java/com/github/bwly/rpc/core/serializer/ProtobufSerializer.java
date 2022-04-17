package com.github.bwly.rpc.core.serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

public class ProtobufSerializer implements Serializer {
    private static final int BUFFER_SIZE = 1024;

    private static final LinkedBuffer BUFFER = LinkedBuffer.allocate(BUFFER_SIZE);

    @Override
    public <T> byte[] serialize(T object) {
        Class<T> clazz = (Class<T>) object.getClass();
        Schema<T> schema = RuntimeSchema.getSchema(clazz);

        try {
            return ProtostuffIOUtil.toByteArray(object, schema, BUFFER);
        }
        finally {
            BUFFER.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }
}
