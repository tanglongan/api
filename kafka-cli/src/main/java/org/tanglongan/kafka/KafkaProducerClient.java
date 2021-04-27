package org.tanglongan.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaProducerClient {


    public static Properties initConfig() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.BROKER_LIST);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, KafkaConstant.PRODUCER_CLIENT_ID);
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomProducerPartitioner.class.getName()); //使用自定义分区器
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,CustomProducerInterceptor.class.getName());//使用自定义拦截器
        return props;
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        KafkaProducer<String, String> producer = new KafkaProducer<>(initConfig());
        ProducerRecord<String, String> record = new ProducerRecord<>(KafkaConstant.TOPIC_DEMO_01, "Hello Kafka!");
        //发送消息1（发后即忘）
        producer.send(record);

        //发送消息2（同步发送）
        Future<RecordMetadata> future = producer.send(record);
        RecordMetadata metadata = future.get();
        System.out.println(metadata.topic() + "--" + metadata.partition() + ":" + metadata.offset());

        //发送消息3（异步发送）
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                } else {
                    System.out.println(metadata.topic() + "--" + metadata.partition() + ":" + metadata.offset());
                }
            }
        });
    }

}
