package com.imooc.coupon.advice;

import com.imooc.coupon.enums.CouponExceptionEnum;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * Global Exception Handling
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(HttpServletRequest request, CouponException ex){
        CommonResponse<String> response = new CommonResponse<>(CouponExceptionEnum.TEST_ERROR.getCode(), "business error");
        response.setData(ex.getMessage());
        return response;
    }
}
