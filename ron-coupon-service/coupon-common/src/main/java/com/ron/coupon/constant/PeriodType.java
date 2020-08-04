package com.ron.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.iterators.PeekingIterator;

import java.time.Period;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Valid Period Types. 有效期类型枚举
 */
@Getter
@AllArgsConstructor
public enum PeriodType {

    /** The valid period is universally fixed 一开始就规定好有效日期*/
    REGULAR("Fixed date", 1),
    /** valid for x days from distributed date 比如从领取日期起7日内*/
    SHIFT("Flexible date(Period will be calculated when distributed", 2);

    /** Decribe the Period*/
    private String description;

    /** Period code*/
    private Integer code;

    public static PeriodType of(Integer code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " node exist!"));
    }
}
