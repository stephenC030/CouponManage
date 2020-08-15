package com.ron.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/** Status of user coupon
 *
 */
@Getter
@AllArgsConstructor
public enum CouponStatus {

    USABLE("the couple is usable", 1),
    USED("Already used", 2),
    EXPIRED("Expired and was not used", 3);

    /** Description of coupon status */
    private String description;
    /** Code for coupon status */
    private Integer code;
    /** Get CouponStatus according to code */
    public static CouponStatus of(Integer code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists")
                );
    }
}
