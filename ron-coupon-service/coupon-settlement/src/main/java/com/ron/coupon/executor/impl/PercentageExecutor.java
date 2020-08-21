package com.ron.coupon.executor.impl;

import com.ron.coupon.constant.RuleFlag;
import com.ron.coupon.executor.AbstractExecutor;
import com.ron.coupon.executor.RuleExecutor;
import com.ron.coupon.vo.CouponTemplateSDK;
import com.ron.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Executor for Coupon of type Percentage %X off total price */
@Component
@Slf4j
public class PercentageExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.PERCENT_OFF;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlementInfo) {
        Double originalTotalPrice = retain2Decimals(
                goodsCostSum(settlementInfo.getGoodsInfos()));
        SettlementInfo notSatisfiedInfo = processGoodsTypeNotSatisfy(
                settlementInfo, originalTotalPrice);
        if(null != notSatisfiedInfo){
            log.debug("[Percentage Coupon] Goods type not matched with requirement");
            return notSatisfiedInfo;
        }
        /// There is no total price requirement for percentage off coupon, we can directly use it
        CouponTemplateSDK coupon = settlementInfo.
                getCouponAndTemplateInfos().get(0).getTemplate();
        double quota = coupon.getRule().getDiscount().getQuota();
        double finalPrice = Math.max(originalTotalPrice * quota * 1.0 / 100, minCost());
        settlementInfo.setCost(finalPrice);
        log.debug("Percentage Coupon Successfuly applies! original Price: {}, final Price: {}",
                originalTotalPrice, settlementInfo.getCost());

        return settlementInfo;
    }
}
