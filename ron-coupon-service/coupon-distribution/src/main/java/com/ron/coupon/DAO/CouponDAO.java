package com.ron.coupon.DAO;

import com.ron.coupon.constant.CouponStatus;
import com.ron.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * DAO interface for Coupon
 */
public interface CouponDAO extends JpaRepository<Coupon, Integer> {

    /** Get Coupon records according to userId + CouponStatus */
    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);

}
