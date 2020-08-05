package com.ron.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.ron.coupon.entity.CouponTemplate;
import com.ron.coupon.exception.CouponException;
import com.ron.coupon.service.IBuildTemplateService;
import com.ron.coupon.service.ITemplateBaseService;
import com.ron.coupon.vo.CouponTemplateSDK;
import com.ron.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.eureka.ConditionalOnRibbonAndEurekaEnabled;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Coupon template related function controller
 */
@Slf4j
//@RequestMapping("/template") ///why not use this?
@RestController
public class CouponTemplateController {
    /** Build template service */
    private final IBuildTemplateService buildTemplateService;
    /** Template Base Service */
    private final ITemplateBaseService templateBaseService;

    public CouponTemplateController(IBuildTemplateService buildTemplateService,
                                    ITemplateBaseService templateBaseService) {
        this.buildTemplateService = buildTemplateService;
        this.templateBaseService = templateBaseService;
    }

    /**
     * Build coupon template
     * localhost:7001/coupon-template/template/build
     * localhost:9000/ron-coupon/coupon-template/template/build
     */
    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request)
            throws CouponException{
        log.info("Build template: {}", JSON.toJSONString(request));
        return buildTemplateService.buildTemplate(request);
    }
    /**
     * Build coupon template detail
     * localhost:7001/coupon-template/template/info?id=1
     * localhost:9000/ron-coupon/coupon-template/template/info?id=1
     */
    @GetMapping("/template/info")
    public CouponTemplate buildTemplateInfo(@RequestParam("id") Integer id)
            throws CouponException{
        log.info("Build Template Info For id=: {}", id);
        return templateBaseService.buildTemplateInfo(id);
    }
    /**
     * Find out all available coupon template
     * localhost:7001/coupon-template/template/sdk/all
     * localhost:9000/ron-coupon/coupon-template/template/sdk/all
     */
    @GetMapping("/template/sdk/all")
    public List<CouponTemplateSDK> findAllUsableTemplate(){
        log.info("Find all usable coupon template.");
        return templateBaseService.findAllUsableTemplate();
    }
    /**
     * Get Mapping of template ids to CouponTemplateSDK 获取模版ids到SDK的映射
     * localhost:7001/coupon-template/template/sdk/infos
     * localhost:9000/ron-coupon/coupon-template/template/sdk/infos
     */
    @GetMapping("/template/sdk/infos")
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(@RequestParam("ids") Collection<Integer> ids){
        log.info("Find ids to TemplateSDK for ids= {}", JSON.toJSONString(ids));
        return templateBaseService.findIds2TemplateSDK(ids);
    }
}
