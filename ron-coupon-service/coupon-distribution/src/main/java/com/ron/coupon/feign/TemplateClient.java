package com.ron.coupon.feign;

import com.ron.coupon.feign.hystrix.TemplateClientHystrix;
import com.ron.coupon.vo.CommonResponse;
import com.ron.coupon.vo.CouponTemplateSDK;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Implementation of Feign Interface for calling the template microservice
 * 模版微服务的Feign接口定义
 */
@FeignClient(value = "eureka-client-coupon-template",
        fallback = TemplateClientHystrix.class)
public interface TemplateClient {

    /** Find all Usable Template, calling controller from coupon-template */
    @RequestMapping(value = "/coupon-template/template/sdk/all",
            method = RequestMethod.GET)
    CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate();

    /** Get Mapping of template ids to CouponTemplateSDK 获取模版ids到SDK的映射 */
    @RequestMapping(value = "/coupon-template/template/sdk/infos", method = RequestMethod.GET)
    CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(
            @RequestParam("ids") Collection<Integer> ids);

}
