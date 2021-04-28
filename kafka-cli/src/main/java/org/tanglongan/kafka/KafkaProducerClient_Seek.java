package org.tanglongan.kafka;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaProducerClient_Seek {

    private static final AtomicBoolean isRunning = new AtomicBoolean(true);

    public static Properties initConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.BROKER_LIST);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstant.GROUP_ID);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, KafkaConstant.CONSUMER_CLIENT_ID);
        return props;
    }


    public static void main(String[] args) {
        KafkaProducerClient_Seek client = new KafkaProducerClient_Seek();
        client.consumeSeekFromPosition();
    }

    /**
     * 从分区的指定位置开始消费
     */
    public void consumeSeekFromPosition() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(initConfig());
        consumer.subscribe(Collections.singletonList(KafkaConstant.TOPIC_DEMO_01));

        // 1.消费者客户端需要先调用poll()方法，才能分配到分区
        consumer.poll(Duration.ofMillis(1000));
        // 2.获取消费者端已分配到的分区集
        Set<TopicPartition> topicPartitions = consumer.assignment();
        for (TopicPartition partition : topicPartitions) {
            // 3.从分配到的分区消费位置10处开始消费
            consumer.seek(partition, 10);
        }
        while (isRunning.get()) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(JSON.toJSONString(record));
            }
        }
    }

    /**
     * 从已分配的每个分区的开始位置消费
     */
    public void consumeSeekFromBeginning() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(initConfig());
        consumer.subscribe(Collections.singletonList(KafkaConstant.TOPIC_DEMO_01));

        // 1.消费者客户端需要先调用poll()方法，才能分配到分区
        consumer.poll(Duration.ofMillis(1000));
        // 2.获取消费者端已分配到的分区集
        Set<TopicPartition> topicPartitions = consumer.assignment();
        // 3.从每个分区的开始位置消费
        consumer.seekToBeginning(topicPartitions);

        while (isRunning.get()) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(JSON.toJSONString(record));
            }
        }
    }


    /**
     * 从已分配的每个分区的尾部位置开始消费
     */
    public void consumeSeekFromEnd() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(initConfig());
        consumer.subscribe(Collections.singletonList(KafkaConstant.TOPIC_DEMO_01));

        // 1.消费者客户端需要先调用poll()方法，才能分配到分区
        consumer.poll(Duration.ofMillis(1000));
        // 2.获取消费者端已分配到的分区集
        Set<TopicPartition> topicPartitions = consumer.assignment();
        // 3.从每个分区的尾部位置消费
        consumer.seekToEnd(topicPartitions);

        while (isRunning.get()) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(JSON.toJSONString(record));
            }
        }
    }

}
