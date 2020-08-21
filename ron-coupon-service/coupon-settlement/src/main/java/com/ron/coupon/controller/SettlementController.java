package com.ron.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.ron.coupon.exception.CouponException;
import com.ron.coupon.executor.ExecutorManager;
import com.ron.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for settlement service.
 */
@RestController
@Slf4j
public class SettlementController {
    /** Manager for directing different types of coupon executors */
    private final ExecutorManager executorManager;

    public SettlementController(ExecutorManager executorManager) {
        this.executorManager = executorManager;
    }

    /** Settlement compute interface */
    @PostMapping("/settlement/compute")
    public SettlementInfo computeRule(@RequestBody  SettlementInfo settlementInfo)
            throws CouponException {
        log.info("Settlement service, settlementInfo: {}",
                JSON.toJSONString(settlementInfo));
        return executorManager.computeRule(settlementInfo);
    }
}
