package com.ron.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ron.coupon.constant.CouponStatus;
import com.ron.coupon.converter.CouponStatusConverter;
import com.ron.coupon.serialization.CouponSerialize;
import com.ron.coupon.vo.CouponTemplateSDK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 *  Entity class of Coupon( that adopted by users)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon")
@JsonSerialize(using = CouponSerialize.class)
public class Coupon {

    /** Autoincremented primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /** Logic key used to connect to coupon template(not primary key) 关联优惠券模版的主键（逻辑外键） */
    @Column(name = "templateId", nullable = false)
    private Integer templateId;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    /** Coupon_code generated by async work, get from Redis */
    @Column(name = "coupon_code", nullable = false)
    private String couponCode;
    /** Time that assigned coupon to user, managed by Jpa Auditing */
    @CreatedDate
    @Column(name = "assign_time", nullable = false)
    private Date assignTime;

    @Basic
    @Column(name = "status", nullable = false)
    @Convert(converter = CouponStatusConverter.class)
    private CouponStatus status;

    /** The template info correspond to current coupon 用户优惠券对应的模板信息 */
    @Transient
    private CouponTemplateSDK templateSDK;

    /** Return an invalid coupon */
    public static Coupon invalidCoupon(){
        Coupon coupon = new Coupon();
        coupon.setId(-1);
        return coupon;
    }

    public Coupon(Integer templateId, Long userId,
                  String couponCode, CouponStatus couponStatus){
        this.templateId = templateId;
        this.userId = userId;
        this.couponCode = couponCode;
        this.status = couponStatus;

    }
}
