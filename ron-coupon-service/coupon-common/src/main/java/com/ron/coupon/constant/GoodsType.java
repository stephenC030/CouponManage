package com.ron.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/** Fake Enum for Types of Goods */
@Getter
@AllArgsConstructor
public enum GoodsType {

    ENTERTAINMENT("Goods of Entertainment", 1),
    FRESH_FOOD("Fresh food", 2),
    FURNITURE("Furniture", 3),
    OTHERS("The other goods", 4),
    ALL("Some Coupon can be applied to all kinds of Goods", 5);

    private String description;

    private Integer code;

    public static GoodsType of(Integer code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + " not exists")
                );
    }

}
