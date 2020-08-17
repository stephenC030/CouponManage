package com.ron.coupon.feign.hystrix;

import com.ron.coupon.exception.CouponException;
import com.ron.coupon.feign.SettlementClient;
import com.ron.coupon.vo.CommonResponse;
import com.ron.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Circuit-breaking strategy for Feign interface of settlement service
 * 结算Feign接口的熔断降级策略
 * */
@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {
    @Override
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlementInfo) throws CouponException {
        log.error("[eureka-client-coupon-settlement] computeRule() request Error");
        settlementInfo.setEmploy(false);
        settlementInfo.setCost(-1.0);
        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-settlement] computeRule() request Error",
                settlementInfo
        );
    }
}
