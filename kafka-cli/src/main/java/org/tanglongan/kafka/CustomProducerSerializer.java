package org.tanglongan.kafka;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 自定义序列化器
 */
public class CustomProducerSerializer implements Serializer<String> {

    private String encoding = "UTF-8";

    /**
     * 用来配置当前序列化对象，主要就是用来确定编码
     *
     * @param configs 配置属性
     * @param isKey   消息是否包含Key属性
     */
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

        String propertyName = isKey ? "key.serializer.encoding" : "value.serializer.encoding";
        Object encodingValue = configs.get(propertyName);
        if (encodingValue == null) {
            encodingValue = configs.get("serializer.encoding");
        }
        if (encodingValue instanceof String) {
            encoding = (String) encodingValue;
        }
    }

    /**
     * 序列化操作逻辑方法
     *
     * @param topic 主题名
     * @param data  要序列化的值，可能是消息的key或value
     */
    @Override
    public byte[] serialize(String topic, String data) {
        try {
            if (data == null) {
                return null;
            } else {
                return data.getBytes(encoding);
            }
        } catch (UnsupportedEncodingException e) {
            throw new SerializationException("Error when serializing string to byte[] due to unsupported encoding " + encoding);
        }
    }

    @Override
    public byte[] serialize(String topic, Headers headers, String data) {
        return Serializer.super.serialize(topic, headers, data);
    }

}
