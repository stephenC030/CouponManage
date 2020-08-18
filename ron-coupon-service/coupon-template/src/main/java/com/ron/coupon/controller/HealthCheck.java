package com.ron.coupon.controller;

import com.ron.coupon.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Health check api
 */
@Slf4j
@RestController
public class HealthCheck {
    /** Service Discovery Client 服务发现客户端 */
    private final DiscoveryClient client;
    /** Service registration interface. Get service id through this. 服务注册接口，提供获取服务ID的方法 */
    private final Registration registration;

    @Autowired
    public HealthCheck(DiscoveryClient client, Registration registration) {
        this.client = client;
        this.registration = registration;
    }

    /**
     *  Health Check Interface
     *  localhost:7001/coupon-template/health
     * @return
     */
    @GetMapping("/health")
    public String health(){
        log.debug("view health API");
        return "CouponTemplate Is OK!";
    }

    /**
     * Exception test interface
     * localhost:7001/coupon-template/exception
     * @return
     * @throws CouponException
     */
    @GetMapping("/exception")
    public String exception() throws CouponException{
        log.debug("view exception api");
        throw new CouponException("Coupon Template Has Some Problem");
    }

    /**
     * Get service meta-data from Eureka Server
     * localhost:7001/coupon-template/info
     * @return
     */
    @GetMapping("/info")
    public List<Map<String, Object>> info(){
        List<ServiceInstance> instances =
                client.getInstances(registration.getServiceId());
        List<Map<String, Object>> result = new ArrayList<>(instances.size());
        instances.forEach(i -> {
            Map<String, Object> info = new HashMap<>();
            info.put("serviceId", i.getServiceId());
            info.put("instanceId", i.getInstanceId());
            info.put("port", i.getPort());

            result.add(info);
        });
        return result;
    }
}
