package com.imooc.coupon.enums;

import lombok.Getter;

@Getter
public enum CouponExceptionEnum {
    TEST_ERROR(1, "error for testing exceptionHandler")
    ;
    private Integer code;
    private String message;

    CouponExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
