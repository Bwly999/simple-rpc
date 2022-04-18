package com.github.bwly.rpc.server.core;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ProtobufTest {
    @Test
    public void test() {
        // TODO
        List<Schema> schemas = new ArrayList<>();
        // if schema is not cached, you can use RuntimeSchema.createFrom(clazz) to create it.
        Schema<Foo> schema = RuntimeSchema.getSchema(Foo.class);
        System.out.println(schema);
        schemas.add(RuntimeSchema.getSchema(Foo.class));
        schemas.add(RuntimeSchema.getSchema(Foo.class));
        schemas.add(RuntimeSchema.getSchema(Foo.class));
        schemas.add(RuntimeSchema.getSchema(Foo.class));
        schemas.stream().forEach(System.out::println);
        // Re-use (manage) this buffer to avoid allocating on every serialization
        LinkedBuffer buffer = LinkedBuffer.allocate(512);
        Foo foo = new Foo("hello", "1");
        // ser
        final byte[] protostuff;
        try
        {
            protostuff = ProtostuffIOUtil.toByteArray(foo, schema, buffer);
        }
        finally
        {
            buffer.clear();
        }

        // deser
        Foo fooParsed = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(protostuff, fooParsed, schema);
        System.out.println(fooParsed);
    }
}
