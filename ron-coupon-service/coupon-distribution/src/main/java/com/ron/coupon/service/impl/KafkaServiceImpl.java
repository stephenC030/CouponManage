package com.ron.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.ron.coupon.DAO.CouponDAO;
import com.ron.coupon.constant.Constant;
import com.ron.coupon.constant.CouponStatus;
import com.ron.coupon.entity.Coupon;
import com.ron.coupon.service.IKafkaService;
import com.ron.coupon.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of Kafka related Services
 * Core logic: Synchronized the status change of Coupons in Cache to DB.
 * 将CACHE中的Coupon的状态变化同步到DB
 */
@Slf4j
@Service
public class KafkaServiceImpl implements IKafkaService {
    private final CouponDAO couponDAO;

    public KafkaServiceImpl(CouponDAO couponDAO) {
        this.couponDAO = couponDAO;
    }

    @Override
    @KafkaListener(topics = {Constant.TOPIC}, groupId = "ron-coupon-1")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {

        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()){
            Object message = kafkaMessage.get();
            CouponKafkaMessage couponInfo = JSON.parseObject(
                    message.toString(),
                    CouponKafkaMessage.class
            );
            log.info("Received Coupon Kafka Message: {}", message.toString());
            CouponStatus status = CouponStatus.of(couponInfo.getStatus());
            switch (status){
                case USABLE:
                    break;
                case USED:
                    processUsedCoupons(couponInfo, status);
                    break;
                case EXPIRED:
                    processExpiredCoupons(couponInfo, status);
                    break;
            }
        }

    }
    /** Process Used Coupons */
    private void processUsedCoupons(CouponKafkaMessage kafkaMessage,
                                    CouponStatus status){
        //TODO: Send user text for important notification
        processCouponsByStatus(kafkaMessage, status);
    }
    /** Process Expired Coupons */
    private void processExpiredCoupons(CouponKafkaMessage kafkaMessage,
                                    CouponStatus status){
        //TODO: Just notify user that a coupon is expired
        processCouponsByStatus(kafkaMessage, status);
    }
    /** Process coupons info by status */
    private void processCouponsByStatus(CouponKafkaMessage kafkaMessage,
                                        CouponStatus status){
        List<Coupon> coupons = couponDAO.findAllById(kafkaMessage.getIds());
        if(CollectionUtils.isEmpty(coupons) ||
                coupons.size() != kafkaMessage.getIds().size()){
            log.error("Couldn't find correct coupon info, kafkaMessage: {}",
                    JSON.toJSONString(kafkaMessage));
            //TODO: Send email to responser.
            return;
        }
        coupons.forEach(c -> c.setStatus(status));
        log.info("CouponKafkaMessage Op Coupon count: {}",
                couponDAO.saveAll(coupons).size());


    }
}
