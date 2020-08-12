package com.ron.coupon.service;

import com.alibaba.fastjson.JSON;
import com.ron.coupon.constant.CouponCategory;
import com.ron.coupon.constant.DistributeTarget;
import com.ron.coupon.constant.PeriodType;
import com.ron.coupon.constant.ProductLine;
import com.ron.coupon.vo.TemplateRequest;
import com.ron.coupon.vo.TemplateRule;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * Test for building coupon template. 构造优惠券模板服务测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BuildTemplateTest {

    @Autowired
    private IBuildTemplateService buildTemplateService;

    @Test
    public void testBuildTemplate() throws Exception{
        TemplateRequest tmp = fakeTemplateRequest();
        System.out.println(JSON.toJSONString(
                buildTemplateService.buildTemplate(tmp)
        ));
        // Only for test, give time for async service
        Thread.sleep(5000);
    }

    /** Fake Request for test */
    private TemplateRequest fakeTemplateRequest(){
        TemplateRequest request = new TemplateRequest();
        request.setName("CouponTemplate-"+new Date().getTime());
        request.setLogo("http://www.google.com");
        request.setDesc("A Coupon Template");
        request.setCategory(CouponCategory.DEDUCT_X_OFF_Y.getCode());
        request.setProductLine(ProductLine.AMZ.getCode());
        request.setCount(10000);
        request.setUserId(10001L); //fake user Id
        request.setTarget(DistributeTarget.SINGLE.getCode());

        TemplateRule rule = new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(
                PeriodType.SHIFT.getCode(),
                1, DateUtils.addDays(new Date(), 60).getTime()
        ));
        rule.setDiscount(new TemplateRule.Discount(5, 1));
        rule.setLimitation(1);
        rule.setUsage(new TemplateRule.Usage(
                "CA", "LA",
                JSON.toJSONString(Arrays.asList("Entertainment", "Sports"))
        ));
        rule.setWeight(JSON.toJSONString(Collections.emptyList()));

        request.setRule(rule);
        return request;


    }
}
