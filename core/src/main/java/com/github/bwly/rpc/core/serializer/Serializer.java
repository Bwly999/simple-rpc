package com.github.bwly.rpc.core.serializer;

public interface Serializer {
    /**
     * 序列化
     * @param object 序列号对象
     * @return
     */
    <T> byte[] serialize(T object);

    /**
     * 反序列化
     * @param bytes
     * @param clazz
     * @param <T> 目标类型
     * @return
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
