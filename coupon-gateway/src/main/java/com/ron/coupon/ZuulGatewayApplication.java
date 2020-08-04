package com.ron.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * The Entry of Application.
 * @EnableZuulProxy makes it a Zuul Server.
 * @SpringCloudApplication includes @SpringBootApplicaion, @EnableDiscoveryClient for registration. @EnableCircuitBreaker for future circuit break
 */
@EnableZuulProxy
@SpringCloudApplication
public class ZuulGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulGatewayApplication.class, args);

    }
}
