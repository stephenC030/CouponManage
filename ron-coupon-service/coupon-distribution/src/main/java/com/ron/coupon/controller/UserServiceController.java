package com.ron.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.ron.coupon.entity.Coupon;
import com.ron.coupon.exception.CouponException;
import com.ron.coupon.service.IUserService;
import com.ron.coupon.vo.AcquireTemplateRequest;
import com.ron.coupon.vo.CouponTemplateSDK;
import com.ron.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** API for User Service */
@Slf4j
@RestController
public class UserServiceController {
    private final IUserService userService;

    public UserServiceController(IUserService userService) {
        this.userService = userService;
    }

    /** Get Coupons by userId and coupon status */
    @GetMapping("/coupons")
    public List<Coupon> findCouponsByStatus(@RequestParam("userId") Long userId,
                                            @RequestParam("status") Integer status)
            throws CouponException{
        log.info("Find coupons by Status: userId: {}, status: {}", userId, status);
        return userService.findCouponsByStatus(userId, status);
    }

    /** Get Coupon Templates that a user can acquire */
    @GetMapping("/template")
    public List<CouponTemplateSDK> findAvailableTemplate(@RequestParam("userId") Long userId)
            throws CouponException{
        log.info("Find Coupon Templates that current user can acquire, userId: {}", userId);
        return userService.findAvailableTemplate(userId);
    }

    /** User Acquire a Coupon */
    @PostMapping("/acquire/template")
    public Coupon acquireTemplate(@RequestBody AcquireTemplateRequest request)
            throws CouponException{
        log.info("Acquire Template, request: {}", JSON.toJSONString(request));
        return userService.acquireTemplate(request);
    }

    @PostMapping("/settlement")
    public SettlementInfo settlement(@RequestBody SettlementInfo info)
            throws CouponException{
        log.info("Settlement, info: {}", JSON.toJSONString(info));
        return userService.settlement(info);
    }
}
