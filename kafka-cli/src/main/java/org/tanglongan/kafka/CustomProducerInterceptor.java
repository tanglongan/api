package org.tanglongan.kafka;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 自定义生产者拦截器
 */
public class CustomProducerInterceptor implements ProducerInterceptor<String, String> {
    private AtomicLong sendSuccess = new AtomicLong(0);
    private AtomicLong sendFailure = new AtomicLong(0);

    /**
     * 在消息序列化和计算分区号之前调用
     */
    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        String modifiedValue = "prefix1-" + record.value();
        return new ProducerRecord<>(record.topic(), record.partition(), record.timestamp(), record.key(), modifiedValue, record.headers());
    }

    /**
     * 消息被应答之前或消息发送失败时调用生产者拦截的该方法
     */
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception == null) {
            sendSuccess.incrementAndGet();
        } else {
            sendFailure.incrementAndGet();
        }
    }

    /**
     * 在关闭拦截器时执行一些资源的清理工作
     */
    @Override
    public void close() {
        long s = sendSuccess.longValue();
        long f = sendFailure.longValue();
        double ratio = (double) s / (s + f);
        System.out.println("发送成功率：" + String.format("%f", ratio * 100) + "%");
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }
}
