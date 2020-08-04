package com.ron.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ProductLine {

    AMZ("Amazon", 1),
    EBY("Ebay", 2);

    /** Description for product line*/
    private String description;
    /*Code for identifying product line*/
    private Integer code;

    /** Find the product line according to code*/
    public static ProductLine of(Integer code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " node exist!"));
    }
}
