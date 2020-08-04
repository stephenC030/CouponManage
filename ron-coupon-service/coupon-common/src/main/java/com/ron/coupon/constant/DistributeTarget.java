package com.ron.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * The target we distribute the coupon to. 分发目标
 */
@Getter
@AllArgsConstructor
public enum DistributeTarget {

    /** The user have to actively ask for the coupon*/
    SINGLE("Single User", 1),
    /** System distribute the coupon to a group of users */
    MULTI("Multiple Users", 2);

    /** Decribe the target*/
    private String description;

    /** target code*/
    private Integer code;

    public static DistributeTarget of(Integer code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " node exist!"));
    }
}
