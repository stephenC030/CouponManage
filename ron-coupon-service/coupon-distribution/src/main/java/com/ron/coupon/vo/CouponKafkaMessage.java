package com.ron.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Definition of Kafka message object
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponKafkaMessage {
    /** Status of coupon */
    private Integer status;
    /** Primary keys of Coupons */
    private List<Integer> ids;
}
