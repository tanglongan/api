package org.tanglongan.kafka;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaConsumerClient_Manual {

    private static final AtomicBoolean isRunning = new AtomicBoolean(true);

    public static Properties initConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.BROKER_LIST);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstant.GROUP_ID);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, KafkaConstant.CONSUMER_CLIENT_ID);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);//关闭自动提交
        return props;
    }


    public static void main(String[] args) {
        KafkaConsumerClient_Manual client = new KafkaConsumerClient_Manual();
        client.consumerAndCommitSync();
    }


    /**
     * 方式一：同步提交不带参数的消费位移
     * 先对每次poll的消息集进行处理，处理完成之后再对整个消息集做同步提交
     * commitSync()不带参数，只能提交当前批次对应的position值，不够精确，可能存在重复消费的情况
     */
    public void consumerAndCommitSync() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(initConfig());
        consumer.subscribe(Collections.singletonList(KafkaConstant.TOPIC_DEMO_01));
        try {
            while (isRunning.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                //1.对poll的消息集进行处理
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(JSON.toJSONString(record));
                }
                //2.同步提交本批次的消费位移
                consumer.commitSync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }


    /**
     * 方式二：同步提交带参数的消费位移
     * commitSync()带有参数，可以更精准的提交消费位移，但因为commitSync()方法是同步执行的，下面方法每消费一个消息就提交中会将效率降低到一个很低的点
     */
    public void consumerAndCommitSyncWithParameters() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(initConfig());
        consumer.subscribe(Collections.singletonList(KafkaConstant.TOPIC_DEMO_01));
        try {
            while (isRunning.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                // 每处理一条消息，就提交消费位移
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(JSON.toJSONString(record));
                    Map<TopicPartition, OffsetAndMetadata> map = Collections.singletonMap(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));
                    consumer.commitSync(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }


    /**
     * 方式二：按分区粒度，同步提交带参数的消费位移
     */
    public void consumerAndCommitSyncWithParametersByPartition() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(initConfig());
        consumer.subscribe(Collections.singletonList(KafkaConstant.TOPIC_DEMO_01));
        try {
            while (isRunning.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (TopicPartition partition : records.partitions()) {
                    // 按分区处理消息
                    List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
                    for (ConsumerRecord<String, String> record : partitionRecords) {
                        System.out.println(JSON.toJSONString(record));
                    }
                    //按分区提交分区的消费位移
                    long lastConsumeOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                    Map<TopicPartition, OffsetAndMetadata> map = Collections.singletonMap(new TopicPartition(partition.topic(), partition.partition()), new OffsetAndMetadata(lastConsumeOffset + 1));
                    consumer.commitSync(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }


    /**
     * 方式三：异步提交消费位移
     */
    public void consumerAndCommitAsync() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(initConfig());
        consumer.subscribe(Collections.singletonList(KafkaConstant.TOPIC_DEMO_01));
        try {
            while (isRunning.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(JSON.toJSONString(record));
                }

                //当异步提交位移完成之后，会回调onComplete()方法
                consumer.commitAsync((offsets, exception) -> {
                    if (exception == null) {
                        System.out.println(offsets);
                    } else {
                        System.out.println("fail tp commit offset: " + offsets + "\n" + exception);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }


}
