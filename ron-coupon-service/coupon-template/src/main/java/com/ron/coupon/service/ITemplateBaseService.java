package com.ron.coupon.service;

import com.ron.coupon.entity.CouponTemplate;
import com.ron.coupon.exception.CouponException;
import com.ron.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Template basic (View, delete, ...) service definition
 */
public interface ITemplateBaseService {

    /** Get template info with template id
     * @param id Template id
     * @return {@link CouponTemplate} Template Entity
     * */
    CouponTemplate buildTemplateInfo(Integer id) throws CouponException;

    /**
     * Find all available template
     * @return {@link CouponTemplateSDK}s
     */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /**
     * Get Mapping of template ids to CouponTemplateSDK 获取模版到SDK的映射
     * @param ids template ids
     * @return Map<key: template id, value: CouponTemplateSDK>
     */
    Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);
}
