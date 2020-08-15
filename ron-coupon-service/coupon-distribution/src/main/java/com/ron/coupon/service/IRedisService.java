package com.ron.coupon.service;

import com.ron.coupon.entity.Coupon;
import com.ron.coupon.exception.CouponException;

import java.util.List;

/** Interface for Services related to Redis
 *  1. 3 Coupon Status related operations in Cache
 *  2. Coupon code related operations in Cache
 * */
public interface IRedisService {

    /**
     * Get all the coupon of a certain status of a user 根据userId和状态找到缓存的COUPON列表
     * @param userId userId
     * @param status  Coupon Status {@link com.ron.coupon.constant.CouponStatus}
     * @return {@link Coupon}s, Could be null, which means no record
     */
    List<Coupon> getCachedCoupons(Long userId, Integer status);

    /**
     * Save a empty list of coupon into Cache. 避免缓存穿透， avoid cache penetration
     * @param userId
     * @param status A List of coupon Status
     */
    void saveEmptyCouponListToCache(Long userId, List<Integer> status);

    /**
     * Get coupon code from Cache according to template Id
     * @param templateId primary key of a template
     * @return CouponCode. Could be null if this type of Coupon is already used up.
     */
    String tryToAcquireCouponCodeFromCache(Integer templateId);

    /**
     * Store the coupon of a certain user into Cache
     * @param userId
     * @param coupons {@link Coupon}s There could be multiple coupons of a user
     * @param status {@link com.ron.coupon.constant.CouponStatus}
     * @return  Number of Successfully saved coupons.
     * @throws CouponException
     */
    Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException;
}
