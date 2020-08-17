package com.ron.coupon.feign;

import com.ron.coupon.exception.CouponException;
import com.ron.coupon.feign.hystrix.SettlementClientHystrix;
import com.ron.coupon.vo.CommonResponse;
import com.ron.coupon.vo.SettlementInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Feign Interface for calling settlement microservice
 */
@FeignClient(value = "eureka-client-coupon-settlement",
        fallback = SettlementClientHystrix.class)
public interface SettlementClient {

    /** Calculation of rule after applying coupons */
    @RequestMapping(value = "/coupon-settlement/settlement/compute", method = RequestMethod.POST)
    CommonResponse<SettlementInfo> computeRule(@RequestBody SettlementInfo settlementInfo)
            throws CouponException;
}
