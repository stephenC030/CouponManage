package com.ron.coupon.controller;

import com.ron.coupon.annotation.IgnoreResponseAdvice;
import com.ron.coupon.feign.TemplateClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Controller for Ribbon application, not for outer public access
 */
@Slf4j
@RestController
public class RibbonController {
    /** Rest客户端 */
    private final RestTemplate restTemplate;

    public RibbonController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /** Invoke template MicroService through Ribbon
     * url: /coupon-distribution/info  */
    @GetMapping("/info")
    @IgnoreResponseAdvice
    public TemplateInfo getTemplateInfo(){
        String infoUrl = "http://eureka-client-coupon-template/coupon-template/info";
        return restTemplate.getForEntity(infoUrl, TemplateInfo.class).getBody();
    }

    /** Meta-data of template service */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TemplateInfo{
        private Integer code;
        private String message;
        private List<Map<String, Object>> data;

    }

}
