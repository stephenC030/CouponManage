package com.ron.coupon.serialization;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ron.coupon.entity.CouponTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Coupon Template Entity class user-defined serializer.
 */
public class CouponTemplateSerialize extends JsonSerializer<CouponTemplate> {
    @Override
    public void serialize(CouponTemplate template,
                          JsonGenerator generator,
                          SerializerProvider serializerProvider)
            throws IOException {
        /// Start Serialization
        generator.writeStartObject();
        generator.writeStringField("id", template.getId().toString());
        generator.writeStringField("name", template.getName());
        generator.writeStringField("logo", template.getLogo());
        generator.writeStringField("desc", template.getDesc());
        generator.writeStringField("category", template.getCategory().getDescription());
        generator.writeStringField("productLine", template.getProductLine().getDescription());
        generator.writeStringField("count", template.getCount().toString());
        generator.writeStringField("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(template.getCreateTime()));
        generator.writeStringField("userId", template.getUserId().toString());
        /** Don't forget to concat id*/
        generator.writeStringField("key", template.getKey() + String.format("%04d", template.getId()));
        generator.writeStringField("target", template.getTarget().getDescription());
        generator.writeStringField("rule", JSON.toJSONString(template.getRule()));

        ///End Serialization
        generator.writeEndObject();
    }
}
