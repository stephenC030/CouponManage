package com.ron.coupon.dao;

import com.ron.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Coupon Template DAO interface
 */
public interface CouponTemplateDao extends JpaRepository<CouponTemplate, Integer> {
    /** find template according to template name */
    CouponTemplate findByName(String name);
    /** find all template with available or not && expired or not */
    List<CouponTemplate> findAllByAvailableAndExpired(Boolean available, Boolean expired);
    /** find all template by expired flag */
    List<CouponTemplate> findAllByExpired(Boolean expired);
}
