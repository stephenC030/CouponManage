package com.ron.coupon.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;

public class CouponException extends Exception {

    public CouponException(String message){
        super(message);
    }
}
