package com.ron.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Object definition for settlement service
 * 1. userId
 * 2. (List of)Goods Info
 * 3. List of Coupons
 * 4. Settlement result : final price
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementInfo {
    private Long userId;
    private List<GoodsInfo> goodsInfos;
    /** List of Coupon */
    private List<CouponAndTemplateInfo> couponAndTemplateInfos;
    /** true: Actually Use the coupon. False: just for calculating the final price
     *  是否使结算生效，TRUE即核销,即要实际使用该券 FALSE代表结算，即只是计算出如果用的话的最后价格*/
    private Boolean employ;
    /** The final result price of settlement */
    private Double cost;

    /** Info class of Coupon and Template */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponAndTemplateInfo{
        /** Primary key for Coupon */
        private Integer id;
        /** Corresponding Template for coupon */
        private CouponTemplateSDK template;
    }
}
