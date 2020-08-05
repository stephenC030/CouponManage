package com.ron.coupon.service;

import com.ron.coupon.entity.CouponTemplate;

public interface IAsyncService {
    /** create coupon code according to template asynchronously
     * @param template {@link CouponTemplate} Coupon Template Entity
     */
    void asyncConstructCouponByTemplate(CouponTemplate template);


}
