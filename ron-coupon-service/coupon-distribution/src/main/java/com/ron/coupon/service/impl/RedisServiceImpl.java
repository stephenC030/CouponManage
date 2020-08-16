package com.ron.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.netflix.discovery.converters.Auto;
import com.ron.coupon.constant.Constant;
import com.ron.coupon.constant.CouponStatus;
import com.ron.coupon.entity.Coupon;
import com.ron.coupon.exception.CouponException;
import com.ron.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Implementation of Redis related service interface
 */
@Service
@Slf4j
public class RedisServiceImpl implements IRedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Coupon> getCachedCoupons(Long userId, Integer status) {
        log.info("Get Coupons from Cache: userId: {}, status: {}", userId, status);
        String redisKey = status2RedisKey(status, userId);
        List<String> couponStrs = redisTemplate.opsForHash().values(redisKey)
                .stream()
                .map(o -> Objects.toString(o, null))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(couponStrs)){
            saveEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Collections.emptyList();
        }
        return couponStrs.stream()
                .map(cs -> JSON.parseObject(cs, Coupon.class))
                .collect(Collectors.toList());

    }

    /** Avoid Cache penetration */
    @Override
    @SuppressWarnings(value = "all")
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("Save Empty list to cahce for User: {}, Status: {}", userId, JSON.toJSONString(status));
        /** key: Coupon_id, value: Serialized Coupon Object */
        Map<String, String> invalidCouponMap = new HashMap<>();
        /** Use -1 as key to represent invalid coupon */
        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));

        ////Use SessionCallback to put data into Redis pipeline
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                status.forEach(s -> {
                    String redisKey = status2RedisKey(s, userId);
                    redisOperations.opsForHash().putAll(redisKey, invalidCouponMap);

                });
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));

    }

    /** Get Redis key based on status */
    private String status2RedisKey(Integer status, Long userId){
        String redisKey = null;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus){
            case USABLE:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USEABLE, userId);
                break;
            case USED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USED, userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_EXPIRED, userId);
                break;
        }
        return redisKey;
    }

    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, templateId.toString());
        // We don't care about the order of coupon code, so either leftPop or rightPop is OK.
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);
        log.info("Acquire Coupon Code: templateId: {}, redisKey: {}, couponCode: {}", templateId, redisKey, couponCode);
        return couponCode;
    }

    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
        log.info("Add Coupon To Cache: userId:{}, coupons:{}, status: {}", userId, JSON.toJSONString(coupons), status);
        Integer result = -1;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus){
            case USABLE:
                result = addCouponToCacheForUsable(userId, coupons);
                break;
            case EXPIRED:
                result = addCouponToCacheForExpired(userId, coupons);
                break;
            case USED:
                result = addCouponToCacheForUsed(userId, coupons);
                break;
        }
        return result;
    }
    /** Add new coupon to cahce */
    private Integer addCouponToCacheForUsable(Long userId, List<Coupon> coupons){
        // If coupon is USABLE, it is newly adopted coupon, so only affects the cache of USER_COUPON_USABLE
        log.debug("Add Coupon To Cache For Usable");
        Map<String, String> needCachedObject = new HashMap<>();
        coupons.forEach(c ->
                needCachedObject.put(
                        c.getId().toString(),
                        JSON.toJSONString(c)
                ));
        String redisKey = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        redisTemplate.opsForHash().putAll(redisKey, needCachedObject);
        log.info("Add {} Coupons to Cache: userId: {}, redisKey: {}",
                needCachedObject.size(), userId, redisKey);
        redisTemplate.expire(
                redisKey, getRandomExpirationTime(1, 2),
                TimeUnit.SECONDS
        );
        return needCachedObject.size();
    }
    /** Add Already Used Coupons to Cache */
    @SuppressWarnings(value = "all")
    private Integer addCouponToCacheForUsed(Long userId, List<Coupon> coupons)
            throws CouponException{
        /** Is status is USED, the operation is using current coupon,
         *  will affect 2 Cache: USABLE, USED
          */
        log.info("Add Coupon to Cache for Used");
        Map<String, String> needCachedForUsed = new HashMap<>(coupons.size());
        String redisKeyForUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId
        );
        String redisKeyForUsed = status2RedisKey(
                CouponStatus.USED.getCode(), userId
        );
        /// Get USABLE coupons of current user
        List<Coupon> curUsableCoupons = getCachedCoupons(userId, CouponStatus.USABLE.getCode());
        /// Number of USABLE coupons must be larger than 1.
        // (Not >= becuz there is a empty invalid coupon for avoiding cache penetration
        assert curUsableCoupons.size() > coupons.size();
        coupons.forEach(c -> needCachedForUsed.put(
                c.getId().toString(),
                JSON.toJSONString(c)
        ));
        /// Match the info of current coupon with which we got from Cache
        List<Integer> curUsableIds = curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        if(!org.apache.commons.collections4.CollectionUtils.isSubCollection(paramIds, curUsableIds)){
            log.error("Current coupons is not equal to Cahce: " +
                    "userId: {}, curUsableIds: {}, passed in Ids: {}",
                    userId, JSON.toJSONString(curUsableIds), JSON.toJSONString(paramIds));
            throw new CouponException("Current Coupons not equal to we got from Cache!");
        }
        List<String> needCleanKeys = paramIds.stream()
                .map(i -> i.toString()).collect(Collectors.toList());
        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public Objects execute(RedisOperations redisOperations) throws DataAccessException {
                ///1. Used Coupons caching
                redisOperations.opsForHash().putAll(
                        redisKeyForUsed, needCachedForUsed
                );
                ///2. USABLE Cache cleaning/updating
                redisOperations.opsForHash().delete(
                        redisKeyForUsable, needCleanKeys.toArray()
                );
                ///3. Update Expiration time
                redisOperations.expire(
                        redisKeyForUsable,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                redisOperations.expire(
                        redisKeyForUsed,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );

                return null;
            }
        };
        log.info("Pipeline Exe Result: {}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();

    }
    /** Add the expired coupons to expired */
    @SuppressWarnings(value = "all")
    private Integer addCouponToCacheForExpired(Long userId, List<Coupon> coupons)
            throws CouponException{
        // Expired Status, so the USABLE coupon is expired.
        // Affects 2 cache: USABLE, EXPIRED.
        log.debug("Add Coupon to Cache for Expired");
        // The cache we have to save in the end
        Map<String, String> needCachedForExpired = new HashMap<>(coupons.size());
        String redisKeyForUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId
        );
        String redisKeyForExpired = status2RedisKey(
                CouponStatus.EXPIRED.getCode(), userId
        );
        List<Coupon> curUsableCoupons = getCachedCoupons(userId, CouponStatus.USABLE.getCode());
        List<Coupon> curExpiredCoupons = getCachedCoupons(userId, CouponStatus.EXPIRED.getCode());
        /// Current Usable coupon numbers must be larger than 1.
        assert curUsableCoupons.size() > coupons.size();

        coupons.forEach(c -> needCachedForExpired.put(
                c.getId().toString(), JSON.toJSONString(c)
        ));
        ///Match the info of current coupons and those in cache
        List<Integer> curUsableIds = curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        if(!org.apache.commons.collections4.CollectionUtils.isSubCollection(paramIds, curUsableCoupons)){
            log.error("Current coupons is not matched with those in Cache. " +
                    "userId: {}, curUsableIds: {}, paramIds: {}",
                    userId, JSON.toJSONString(curUsableIds), JSON.toJSONString(paramIds));
            throw new CouponException("Current coupons is not matched with those in Cache");
        }

        List<String> needCleanKeys = paramIds.stream()
                .map(i -> i.toString()).collect(Collectors.toList());
        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public Objects execute(RedisOperations redisOperations) throws DataAccessException {
                //1. Caching expired coupons
                redisOperations.opsForHash().putAll(
                        redisKeyForExpired, needCachedForExpired
                );
                //2. Cleaning Usable Coupons
                redisOperations.opsForHash().delete(
                        redisKeyForUsable, needCleanKeys.toArray()
                );
                //3. Update Exipiration time
                redisOperations.expire(
                        redisKeyForUsable,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                redisOperations.expire(
                        redisKeyForExpired,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }
    /** Get a random expiration time. Avoid cache stampeding herd
     * @param min Minimum hour
     * @param max Maximum hour
     * @return a random number of second between [min, max]
     * */
    private Long getRandomExpirationTime(Integer min, Integer max){
        return RandomUtils.nextLong(
                min * 60 * 60,
                max * 60 * 60
        );

    }
}
