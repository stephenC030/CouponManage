package com.ron.coupon.feign.hystrix;

import com.ron.coupon.feign.TemplateClient;
import com.ron.coupon.vo.CommonResponse;
import com.ron.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/** Circuit-breaking strategy for Feign interface of Template service
 * 模版Feign接口的熔断降级策略
 * */
@Slf4j
@Component
public class TemplateClientHystrix implements TemplateClient {
    @Override
    public CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate() {
        log.error("[eureka-client-coupon-template] findAllUsableTemplate() request Error");

        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-template] findAllUsableTemplate() request error",
                Collections.emptyList()
        );
    }

    @Override
    public CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(Collection<Integer> ids) {
        log.error("[eureka-client-coupon-template] findIds2TemplateSDK() request error");
        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-template] findIds2TemplateSDK() request error",
                new HashMap<>()
        );
    }
}
