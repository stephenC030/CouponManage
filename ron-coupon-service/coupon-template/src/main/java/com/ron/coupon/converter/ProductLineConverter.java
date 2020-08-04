package com.ron.coupon.converter;

import com.ron.coupon.constant.ProductLine;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.persistence.criteria.CriteriaBuilder;

@Converter
public class ProductLineConverter implements AttributeConverter<ProductLine, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ProductLine productLine) {
        return productLine.getCode();
    }

    @Override
    public ProductLine convertToEntityAttribute(Integer integer) {
        return ProductLine.of(integer);
    }
}

