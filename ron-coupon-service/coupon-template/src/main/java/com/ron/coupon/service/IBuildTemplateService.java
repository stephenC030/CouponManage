package com.ron.coupon.service;

import com.ron.coupon.entity.CouponTemplate;
import com.ron.coupon.exception.CouponException;
import com.ron.coupon.vo.TemplateRequest;
import com.ron.coupon.vo.TemplateRule;

public interface IBuildTemplateService {

    /**
     * Build coupon Template
     * @param request {@link TemplateRequest} Template info request object
     * @return {@link CouponTemplate} Coupon Template Entity object
     * */
    CouponTemplate buildTemplate(TemplateRequest request) throws CouponException;
}
