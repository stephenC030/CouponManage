package com.ron.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * The Entry of Settlement Application
 */
@SpringBootApplication
@EnableEurekaClient
public class SettlementApplication {

    public static void main (String[] args){
        SpringApplication.run(SettlementApplication.class, args);
    }
}
