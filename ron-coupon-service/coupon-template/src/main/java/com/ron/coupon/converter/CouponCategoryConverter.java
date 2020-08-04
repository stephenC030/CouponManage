package com.ron.coupon.converter;

import com.ron.coupon.constant.CouponCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Coupon category enum properties converter
 * AttributeConverter<X, Y>
 *     X: Entity class type
 *     Y: SQL column type
 */
@Converter
public class CouponCategoryConverter
        implements AttributeConverter<CouponCategory, String> {
    /** Convert X to Y to store in DB. Used when insert and update DB. */
    @Override
    public String convertToDatabaseColumn(CouponCategory couponCategory) {
        return couponCategory.getCode();
    }
    /** Convert String Y from DB back to X. Used when Look up in DB */
    @Override
    public CouponCategory convertToEntityAttribute(String s) {
        return CouponCategory.of(s);
    }
}
