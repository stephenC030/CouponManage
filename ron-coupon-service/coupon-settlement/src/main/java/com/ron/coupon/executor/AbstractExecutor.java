package com.ron.coupon.executor;

import com.alibaba.fastjson.JSON;
import com.ron.coupon.vo.GoodsInfo;
import com.ron.coupon.vo.SettlementInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract class of Executor
 */
public class AbstractExecutor {

    /**
     * Verify if the goods type are matched with coupon Rules
     * NOTICE:
     * 1. This is only for single Coupon type. Will @Override for multiple Coupons type.
     * 只实现单品类优惠券的校验，多品类的重写此方法
     * 2. In the products, as long as one of them matches the required type from Coupon Rule is fine
     * 商品里只要有一个优惠券要求的商品类型匹配了就行
     * @param settlementInfo
     * @return
     */
    @SuppressWarnings(value = "all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlementInfo){

        List<Integer> goodsType = settlementInfo.getGoodsInfos()
                .stream().map(GoodsInfo::getType).collect(Collectors.toList());
        // Only take one coupon
        List<Integer> couponGoodsType = JSON.parseObject(
                settlementInfo.getCouponAndTemplateInfos().get(0)
                        .getTemplate().getRule().getUsage().getGoodsType(), List.class);
        /// As long as there is intersection between this two list, valid to use.
        return CollectionUtils.isNotEmpty(
                CollectionUtils.intersection(goodsType, couponGoodsType)
        );
    }

    /**
     * Handle the situation when goods type not match that required by Coupon
     * @param settlementInfo {@link SettlementInfo} Settlement info passed by User
     * @param goodsSum Total price of Products
     * @return {@link SettlementInfo} Modified Settlement Info, if return not null, means not satisfied
     */
    protected SettlementInfo processGoodsTypeNotSatisfy(
            SettlementInfo settlementInfo, double goodsSum){
            boolean isGoodsTypeSatisfy = isGoodsTypeSatisfy(settlementInfo);

            /** When not satisfied, clear the coupon list, return original total price */
            if(!isGoodsTypeSatisfy){
                settlementInfo.setCost(goodsSum);
                settlementInfo.setCouponAndTemplateInfos(Collections.emptyList());
                return settlementInfo;
            }
            return null;
    }

    /** Total price of products */
    protected double goodsCostSum(List<GoodsInfo> goodsInfos){
        return goodsInfos.stream().mapToDouble(g -> g.getCount() * g.getPrice()).sum();
    }

    /** Retain the value to 2 bit after decimal point */
    protected double retain2Decimals(double value){
        return new BigDecimal(value).setScale(
                2, BigDecimal.ROUND_HALF_UP
        ).doubleValue();
    }

    /** Minimum cost of products, avoid getting minus or 0 total price */
    protected double minCost(){
        return 0.1;
    }
}
