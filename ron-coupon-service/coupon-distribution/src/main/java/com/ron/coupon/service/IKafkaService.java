package com.ron.coupon.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Interface for Kafka Related Services
 */
public interface IKafkaService {
    /**
     *  Records for consuming coupon messages 消费优惠券的KAFKA消息
     * @param record {@link ConsumerRecord}
     */
    void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record);
}
