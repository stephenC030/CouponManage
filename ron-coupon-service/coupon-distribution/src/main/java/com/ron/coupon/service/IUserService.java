package com.ron.coupon.service;

import com.ron.coupon.entity.Coupon;
import com.ron.coupon.exception.CouponException;
import com.ron.coupon.vo.AcquireTemplateRequest;
import com.ron.coupon.vo.CouponTemplateSDK;
import com.ron.coupon.vo.SettlementInfo;

import java.util.List;

/**
 * Interface for services related to User
 * 1. Demonstration of three types of status of user coupons
 * 2. Show the coupon templates that user can adopt 查看用户可以领取的优惠券模版
 *       ----- in cooperate with Coupon-template microservice
 * 3. User Adopting Coupon service
 * 4. User using the Coupon --- in cooperate with Coupon-settlement microservice
 */
public interface IUserService {
    /**
     * Look up the coupon record according to userId and coupon status
     * @param userId
     * @param status Coupon Status
     * @return {@link Coupon}s
     * @throws CouponException
     */
    List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException;

    /**
     * Find out All available coupon templates that current user can adopt.
     * @param userId
     * @return {@link CouponTemplateSDK}s
     * @throws CouponException
     */
    List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException;

    /**
     * User request to adopt a coupon
     * @param request {@link AcquireTemplateRequest}
     * @return {@link Coupon}
     * @throws CouponException
     */
    Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException;

    /**
     * Settlement the price after using the coupon
     * @param info {@link SettlementInfo}
     * @return {@link SettlementInfo}
     * @throws CouponException
     */
    SettlementInfo settlement(SettlementInfo info) throws CouponException;
}
