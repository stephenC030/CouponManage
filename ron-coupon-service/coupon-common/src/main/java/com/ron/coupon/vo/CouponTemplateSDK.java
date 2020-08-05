package com.ron.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Coupon Template Info between Microservices. 微服务之间用的优惠券模版信息定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplateSDK {
    /** primary key */
    private Integer id;
    private String name;
    private String logo;
    private String desc;
    private String category;
    /** Code of productLine */
    private Integer productLine;
    /** Coupon Template key */
    private String key;
    private Integer target;
    private TemplateRule rule;
}
