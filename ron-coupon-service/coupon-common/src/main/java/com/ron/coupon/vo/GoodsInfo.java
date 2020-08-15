package com.ron.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Detailed Information for Goods
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfo {

    /** Types of Goods {@link com.ron.coupon.constant.GoodsType} */
    private Integer type;

    private Double price;

    private Integer count;

    //TODO: Goods name, etc...
}
