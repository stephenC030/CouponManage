package com.ron.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Definition of the request for Coupon 获取优惠券请求对象定义
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcquireTemplateRequest {

    private Long userId;
    /** The info of a certain Coupon Template */
    private CouponTemplateSDK templateSDK;
}
