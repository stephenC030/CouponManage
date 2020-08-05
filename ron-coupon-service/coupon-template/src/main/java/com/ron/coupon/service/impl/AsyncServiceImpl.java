package com.ron.coupon.service.impl;

import com.netflix.discovery.converters.Auto;
import com.ron.coupon.constant.Constant;
import com.ron.coupon.dao.CouponTemplateDao;
import com.ron.coupon.entity.CouponTemplate;
import com.ron.coupon.service.IAsyncService;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {
    /** CouponTemplate Dao */
    private final CouponTemplateDao templateDao;
    /** Inject Redis Template class*/
    private final StringRedisTemplate redisTemplate;

    public AsyncServiceImpl(CouponTemplateDao templateDao, StringRedisTemplate redisTemplate) {
        this.templateDao = templateDao;
        this.redisTemplate = redisTemplate;
    }
    /**
     * Asynchronously Create coupon code according to template
     * @param template {@link CouponTemplate} Coupon Template Entity
     */
    @Async("getAsyncExecutor")
    @Override
    @SuppressWarnings("all")
    public void asyncConstructCouponByTemplate(CouponTemplate template) {

        StopWatch watch = StopWatch.createStarted();
        Set<String> couponCodes = buildCouponCode(template);
        ///Will be "ron_coupon_template_code_1"
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE, template.getId().toString());
        log.info("Push CouponCode to Redis:{}",
                redisTemplate.opsForList().rightPushAll(redisKey, couponCodes));
        template.setAvailable(true);
        templateDao.save(template);
        watch.stop();
        log.info("Construct Coupon Code By Template, cost: {}ms",
                watch.getTime(TimeUnit.MILLISECONDS));
        //TODO: Send Email or text to notify the employee that this template is available
        log.info("CouponTemplate({}) IS AVAILABLE!", template.getId());
    }

    /**
     * Create coupon code 构建优惠券码.
     * Each coupon has unique code: in total 18 digits.
     * 对应每一张优惠券一共18位：
     * 4(ProductLine + category) + 6(Date yymmdd) + 8(random number with 0~9)
     * 4（产品线+类型）+ 6(日期随机） + 8（0～9随机数构成）
     * @param template
     * @return
     */
    @SuppressWarnings("all")
    private Set<String> buildCouponCode(CouponTemplate template){
        StopWatch watch = StopWatch.createStarted();
        Set<String> result = new HashSet<>(template.getCount());
        //first 4 digit
        String prefix4 = template.getProductLine().getCode().toString()
                + template.getCategory().getCode();
        String date = new SimpleDateFormat("yyMMdd")
                .format(template.getCreateTime());
        for(int i=0; i != template.getCount(); ++i){
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        while(result.size() < template.getCount()){
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        assert result.size() == template.getCount();
        watch.stop();
        log.info("Build Coupon Code Cost: {}ms",
                watch.getTime(TimeUnit.MILLISECONDS));
        return result;
    }

    /**
     * Create last 14 digits for coupon code
     * @param date Date when template was created.
     * @return last 14 digits of coupon code
     */
    private String buildCouponCodeSuffix14(String date){
        /** Avoid 0 to be the first */
        char[] bases = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        /** middle 6 digits */
        List<Character> chars = date.chars().mapToObj(e ->(char) e).collect(Collectors.toList());
        Collections.shuffle(chars);
        String mid6 = chars.stream()
                .map(Object::toString).collect(Collectors.joining());
        /** last 8 */
        String suffix8 = RandomStringUtils.random(1, bases)
                + RandomStringUtils.randomNumeric(7);
        return mid6 + suffix8;
    }
}
