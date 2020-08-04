package com.ron.coupon.converter;

import com.ron.coupon.constant.DistributeTarget;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.print.attribute.Attribute;

@Converter
public class DistributeTargetConverter implements AttributeConverter<DistributeTarget, Integer> {
    @Override
    public Integer convertToDatabaseColumn(DistributeTarget distributeTarget) {
        return distributeTarget.getCode();
    }

    @Override
    public DistributeTarget convertToEntityAttribute(Integer integer) {
        return DistributeTarget.of(integer);
    }
}
