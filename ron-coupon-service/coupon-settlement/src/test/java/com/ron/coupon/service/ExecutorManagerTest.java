package com.ron.coupon.service;

import com.alibaba.fastjson.JSON;
import com.ron.coupon.constant.CouponCategory;
import com.ron.coupon.constant.GoodsType;
import com.ron.coupon.exception.CouponException;
import com.ron.coupon.executor.ExecutorManager;
import com.ron.coupon.vo.CouponTemplateSDK;
import com.ron.coupon.vo.GoodsInfo;
import com.ron.coupon.vo.SettlementInfo;
import com.ron.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

/**
 * Test for Executor Manager
 * Test the routing and settlement logic of Executors
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ExecutorManagerTest {

    private Long fakeUserId = 20001L;

    @Autowired
    private ExecutorManager executorManager;

    @Test
    public void testComputeRule() throws CouponException{
//        /// Test for Discount Coupon
//        log.info("Test for Discount Coupon");
//        SettlementInfo discountInfo = fakeDiscountSettlementInfo();
//        SettlementInfo result = executorManager.computeRule(discountInfo);
//        log.info("Cost: {}", result.getCost());
//        log.info("Coupon Size: {}", result.getCouponAndTemplateInfos().size());
//        log.info("Coupons detail: {}", result.getCouponAndTemplateInfos());
//
//        /// Test for Percentage Coupon
//        log.info("Test for Percentage Coupon");
//        SettlementInfo percentageInfo = fakePercentageSettlementInfo();
//        SettlementInfo resultPercentage = executorManager.computeRule(percentageInfo);
//        log.info("Cost: {}", resultPercentage.getCost());
//        log.info("Coupon Size: {}", resultPercentage.getCouponAndTemplateInfos().size());
//        log.info("Coupons detail: {}", resultPercentage.getCouponAndTemplateInfos());
//
//
//        /// Test for Direct_Deduct Coupon
//        log.info("Test for Direct_Deduct Coupon");
//        SettlementInfo directInfo = fakeDirectSettlementInfo();
//        SettlementInfo resultDirect = executorManager.computeRule(directInfo);
//        log.info("Cost: {}", resultDirect.getCost());
//        log.info("Coupon Size: {}", resultDirect.getCouponAndTemplateInfos().size());
//        log.info("Coupons detail: {}", resultDirect.getCouponAndTemplateInfos());


        /// Test for Percentage + Discount Coupon
        log.info("Test for Percentage + Discount Coupon");
        SettlementInfo discountAndPercentInfo = fakeDiscountAndPercentSettlementInfo();
        SettlementInfo resultdiscountAndPercent = executorManager.computeRule(discountAndPercentInfo);
        log.info("Cost: {}", resultdiscountAndPercent.getCost());
        log.info("Coupon Size: {}", resultdiscountAndPercent.getCouponAndTemplateInfos().size());
        log.info("Coupons detail: {}", resultdiscountAndPercent.getCouponAndTemplateInfos());

    }
    /** Fake settlement info for coupon type of DEDUCT_X_OFF_Y */
    private SettlementInfo fakeDiscountSettlementInfo(){
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.ENTERTAINMENT.getCode());
        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(5);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.FRESH_FOOD.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));
        SettlementInfo.CouponAndTemplateInfo ctInfo = new SettlementInfo.CouponAndTemplateInfo();
        /// Coupon Id
        ctInfo.setId(1);
        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        /// Coupon Template Id
        templateSDK.setId(1);
        templateSDK.setCategory(CouponCategory.DEDUCT_X_OFF_Y.getCode());
        templateSDK.setKey("100120200821");

        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(20, 199));
        rule.setUsage(new TemplateRule.Usage("CA", "RS",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.ENTERTAINMENT.getCode(),
                        GoodsType.FRESH_FOOD.getCode()
                        ))));
        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);
        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));
        return info;
    }

    /** Fake settlement info for coupon type of PERCENT_OFF */
    private SettlementInfo fakePercentageSettlementInfo(){
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.ENTERTAINMENT.getCode());
        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(5);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.FRESH_FOOD.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));
        SettlementInfo.CouponAndTemplateInfo ctInfo = new SettlementInfo.CouponAndTemplateInfo();
        /// Coupon Id
        ctInfo.setId(1);
        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        /// Coupon Template Id
        templateSDK.setId(2);
        templateSDK.setCategory(CouponCategory.PERCENT_OFF.getCode());
        templateSDK.setKey("100120200821");

        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(85, 0));
        rule.setUsage(new TemplateRule.Usage("CA", "RS",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.FURNITURE.getCode(),
                        GoodsType.FRESH_FOOD.getCode()
                ))));
        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);
        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));
        return info;
    }

    /** Fake settlement info for coupon type of DIRECT_DEDUCT */
    private SettlementInfo fakeDirectSettlementInfo(){
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.ENTERTAINMENT.getCode());
        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(5);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.FRESH_FOOD.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));
        SettlementInfo.CouponAndTemplateInfo ctInfo = new SettlementInfo.CouponAndTemplateInfo();
        /// Coupon Id
        ctInfo.setId(1);
        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        /// Coupon Template Id
        templateSDK.setId(3);
        templateSDK.setCategory(CouponCategory.DIRECT_DEDUCT.getCode());
        templateSDK.setKey("100120200821");

        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(30, 0));
        rule.setUsage(new TemplateRule.Usage("CA", "RS",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.FURNITURE.getCode(),
                        GoodsType.FRESH_FOOD.getCode()
                ))));
        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);
        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));
        return info;
    }

    /** Fake settlement info for Combination coupon type of DEDUCT_X_OFF_Y and PERCENT_OFF */
    private SettlementInfo fakeDiscountAndPercentSettlementInfo(){
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.FURNITURE.getCode());
        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(10);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.FRESH_FOOD.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo ctInfo = new SettlementInfo.CouponAndTemplateInfo();
        /// Coupon Id
        ctInfo.setId(1);
        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        /// Coupon Template Id
        templateSDK.setId(1);
        templateSDK.setCategory(CouponCategory.DEDUCT_X_OFF_Y.getCode());
        templateSDK.setKey("100220200821");

        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(30, 199));
        rule.setUsage(new TemplateRule.Usage("CA", "RS",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.FURNITURE.getCode(),
                        GoodsType.ENTERTAINMENT.getCode()
                ))));
        rule.setWeight(JSON.toJSONString(Collections.emptyList()));
        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);

        SettlementInfo.CouponAndTemplateInfo ctInfoPercent = new SettlementInfo.CouponAndTemplateInfo();
        /// Coupon Id
        ctInfoPercent.setId(2);
        CouponTemplateSDK templateSDKPercent = new CouponTemplateSDK();
        /// Coupon Template Id
        templateSDKPercent.setId(2);
        templateSDKPercent.setCategory(CouponCategory.PERCENT_OFF.getCode());
        templateSDKPercent.setKey("100120200821");

        TemplateRule rulePercent = new TemplateRule();
        rulePercent.setDiscount(new TemplateRule.Discount(85, 0));
        rulePercent.setUsage(new TemplateRule.Usage("CA", "RS",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.FURNITURE.getCode(),
                        GoodsType.ENTERTAINMENT.getCode()
                ))));
        rulePercent.setWeight(JSON.toJSONString(
                Collections.singletonList("1002202008210001")
        ));
        templateSDKPercent.setRule(rulePercent);
        ctInfoPercent.setTemplate(templateSDKPercent);


        info.setCouponAndTemplateInfos(Arrays.asList(ctInfo, ctInfoPercent));
        return info;
    }

}
