package com.ron.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum of different types of Coupons
 */
@Getter
@AllArgsConstructor
public enum RuleFlag {

    // Single types
    /** 满减券*/
    DEDUCT_X_OFF_Y("If the total prices reaches a number Y, deduct a certain price X"),
    /** 折扣券*/
    PERCENT_OFF("X% off the price"),
    /** 立减券*/
    DIRECT_DEDUCT("Directly minus $x off the original price"),

    // Combination of multiple types
    /** 满减券 + 折扣券*/
    DISCOUNT_AND_PERCENT("Discount x off total Price of Y + Percentage off");

    //TODO: More combination of coupons

    private String description;

}
