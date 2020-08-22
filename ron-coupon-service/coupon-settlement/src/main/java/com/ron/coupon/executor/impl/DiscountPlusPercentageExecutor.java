package com.ron.coupon.executor.impl;

import com.alibaba.fastjson.JSON;
import com.ron.coupon.constant.CouponCategory;
import com.ron.coupon.constant.RuleFlag;
import com.ron.coupon.executor.AbstractExecutor;
import com.ron.coupon.executor.RuleExecutor;
import com.ron.coupon.vo.GoodsInfo;
import com.ron.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Executor for Combination of Coupon types of Discount %X off $Y and Percentage Off
 */
@Component
@Slf4j
public class DiscountPlusPercentageExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.DISCOUNT_AND_PERCENT;
    }

    /** Implements the goods type checking for Discount and Percentage Coupon
     *  If we want to use multiple diiferent coupons, all goodsType should satisfy requirement */
    @Override
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlementInfo) {
        log.debug("Check Discount and Percentage Coupon requirement of goods type");
        List<Integer> goodsType = settlementInfo.getGoodsInfos().stream()
                .map(GoodsInfo::getType).collect(Collectors.toList());
        List<Integer> couponGoodsType = new ArrayList<>();
        settlementInfo.getCouponAndTemplateInfos().forEach(
                c -> {
                    couponGoodsType.addAll(JSON.parseObject(
                            c.getTemplate().getRule().getUsage().getGoodsType(), List.class));
                }
        );

        // If we want to use multiple diiferent coupons, all goodsType should satisfy requirement
        // So that input goodsType subtraction requiredGoodsType should be null
        return CollectionUtils.isEmpty(CollectionUtils.subtract(goodsType, couponGoodsType));
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlementInfo) {
        Double originalTotalPrice = retain2Decimals(
                goodsCostSum(settlementInfo.getGoodsInfos()));
        SettlementInfo notSatisfiedInfo = processGoodsTypeNotSatisfy(
                settlementInfo, originalTotalPrice);
        if(null != notSatisfiedInfo){
            log.debug("[Discount and Percentage Coupon] Goods type not matched with requirement");
            return notSatisfiedInfo;
        }

        SettlementInfo.CouponAndTemplateInfo DiscountInfo = null;
        SettlementInfo.CouponAndTemplateInfo PercentageInfo = null;
        for (SettlementInfo.CouponAndTemplateInfo ct : settlementInfo.getCouponAndTemplateInfos()) {
            if(ct.getTemplate().getCategory().equals(CouponCategory.DEDUCT_X_OFF_Y.getCode())){
                DiscountInfo = ct;
            }
            else{
                PercentageInfo = ct;
            }
        }
        assert null != DiscountInfo;
        assert null != PercentageInfo;

        // If these two coupons cannot be used together, clear coupon list and return
        // 如果两张券不能一起用，清空优惠券列表并返回
        if(!ifCouponTemplateCanBeAppliedTogether(DiscountInfo, PercentageInfo)){
            settlementInfo.setCost(originalTotalPrice);
            settlementInfo.setCouponAndTemplateInfos(Collections.emptyList());
            log.debug("These Discount and Percentage Coupons cannot be used together!");
            return settlementInfo;
        }

        List<SettlementInfo.CouponAndTemplateInfo> UsedCouponAndTemplateInfoList = new ArrayList<>();
        // First check if we can use Discount Coupon and calculate the price
        double DiscountBaseLine = (double) DiscountInfo.getTemplate().getRule().getDiscount().getBase();
        double DiscountQuota = (double) DiscountInfo.getTemplate().getRule().getDiscount().getQuota();

        double finalPrice = originalTotalPrice;
        if(finalPrice >= DiscountBaseLine){
            finalPrice -= DiscountQuota;
            UsedCouponAndTemplateInfoList.add(DiscountInfo);
        }
        // Then calculate the price after using Percentage Coupon
        double PercentageQuota = (double) PercentageInfo.getTemplate().getRule().getDiscount().getQuota();
        finalPrice = retain2Decimals(finalPrice * PercentageQuota * 1.0 / 100);

        settlementInfo.setCost(finalPrice);
        settlementInfo.setCouponAndTemplateInfos(UsedCouponAndTemplateInfoList);
        log.debug("After trying to use Discount and Percentage Coupon, original Price: {}, final Price: {}",
                originalTotalPrice, finalPrice);
        return settlementInfo;
    }

    /** validate if two coupons can be used together
     * by validating if TemplateRule.weight satisfy condition */
    @SuppressWarnings("all")
    private boolean ifCouponTemplateCanBeAppliedTogether(SettlementInfo.CouponAndTemplateInfo DiscountInfo,
                                                         SettlementInfo.CouponAndTemplateInfo PercentageInfo){
        String DiscountKey = DiscountInfo.getTemplate().getKey() +
                String.format("%04d", DiscountInfo.getTemplate().getId());
        String PercentageKey = PercentageInfo.getTemplate().getKey() +
                String.format("%04d", PercentageInfo.getTemplate().getId());

        List<String> allSharableKeysForDiscount = new ArrayList<>();
        allSharableKeysForDiscount.add(DiscountKey);
        allSharableKeysForDiscount.addAll(JSON.parseObject(
                DiscountInfo.getTemplate().getRule().getWeight(), List.class));

        List<String> allSharableKeysForPercentage = new ArrayList<>();
        allSharableKeysForPercentage.add(PercentageKey);
        allSharableKeysForPercentage.addAll(JSON.parseObject(
                PercentageInfo.getTemplate().getRule().getWeight(), List.class));

        return CollectionUtils.isSubCollection(Arrays.asList(DiscountKey, PercentageKey),
                allSharableKeysForDiscount)
                || CollectionUtils.isSubCollection(Arrays.asList(DiscountKey, PercentageKey),
                allSharableKeysForPercentage);
    }
}
