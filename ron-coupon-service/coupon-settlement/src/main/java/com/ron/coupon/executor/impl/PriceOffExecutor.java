package com.ron.coupon.executor.impl;

import com.ron.coupon.constant.RuleFlag;
import com.ron.coupon.executor.AbstractExecutor;
import com.ron.coupon.executor.RuleExecutor;
import com.ron.coupon.vo.CouponTemplateSDK;
import com.ron.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Executor for coupon type of discount $X when total price reaches $Y
 * 满减优惠券执行器
 */
@Component
@Slf4j
public class PriceOffExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.DEDUCT_X_OFF_Y;
    }
    @Override
    public SettlementInfo computeRule(SettlementInfo settlementInfo) {
        Double originalTotalPrice = retain2Decimals(
                goodsCostSum(settlementInfo.getGoodsInfos()));
        SettlementInfo notSatisfiedInfo = processGoodsTypeNotSatisfy(
                settlementInfo, originalTotalPrice);
        if(null != notSatisfiedInfo){
            log.debug("[Discount Coupon] Goods type not matched with requirement");
            return notSatisfiedInfo;
        }
        // Judge if the original price reaches the required total price
        // 判断满减是否符合折扣标准
        CouponTemplateSDK coupon = settlementInfo.
                getCouponAndTemplateInfos().get(0).getTemplate();
        double baseLine = coupon.getRule().getDiscount().getBase();
        double quota = coupon.getRule().getDiscount().getQuota();
        if(originalTotalPrice < baseLine){
            log.debug("The original Total Price {} doesn't satisfy " +
                            "baseline of Discount Coupon Requirement: {}",
                    originalTotalPrice, baseLine);
            settlementInfo.setCost(originalTotalPrice);
            settlementInfo.setCouponAndTemplateInfos(Collections.emptyList());
            return settlementInfo;
        }
        settlementInfo.setCost(
                retain2Decimals(Math.max(originalTotalPrice - quota, minCost()))
        );
        log.debug("Coupon successfully applied! Original price: {}, final price: {}",
                originalTotalPrice, settlementInfo.getCost());
        return settlementInfo;
    }
}
