package com.ron.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/*
    Coupon Category
 */
@Getter
@AllArgsConstructor
public enum CouponCategory {
    /** 满减券*/
    DEDUCT_X_OFF_Y("If the total prices reaches a number Y, deduct a certain price X", "001"),
    /** 折扣券*/
    PERCENT_OFF("X% off the price", "002"),
    /** 立减券*/
    DIRECT_DEDUCT("Directly minus $x off the original price", "003")
    ;

    /** Description for category*/
    private String description;
    /*Code for identifying category*/
    private String code;

    /** Find the category according to code*/
    public static CouponCategory of(String code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " node exist!"));
    }
}
