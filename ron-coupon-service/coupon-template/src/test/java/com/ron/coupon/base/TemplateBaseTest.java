package com.ron.coupon.base;

import com.alibaba.fastjson.JSON;
import com.ron.coupon.exception.CouponException;
import com.ron.coupon.service.ITemplateBaseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * Test for template base service
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TemplateBaseTest {

    @Autowired
    ITemplateBaseService baseService;

    @Test
    public void testBuildTemplateInfo() throws CouponException{
        System.out.println(JSON.toJSONString(
                baseService.buildTemplateInfo(10)));
        System.out.println(JSON.toJSONString(
                baseService.buildTemplateInfo(50)));
    }

    @Test
    public void testFindAllUsableTemplate(){
        System.out.println(JSON.toJSONString(
                baseService.findAllUsableTemplate()
        ));
    }

    @Test
    public void testFindId2TemplateSDK(){
        System.out.println(JSON.toJSONString(
                baseService.findIds2TemplateSDK(Arrays.asList(10,2,3))
        ));
    }
}
