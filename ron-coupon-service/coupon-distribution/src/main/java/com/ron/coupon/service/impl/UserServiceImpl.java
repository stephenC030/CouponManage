package com.ron.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.ron.coupon.DAO.CouponDAO;
import com.ron.coupon.constant.Constant;
import com.ron.coupon.constant.CouponStatus;
import com.ron.coupon.entity.Coupon;
import com.ron.coupon.exception.CouponException;
import com.ron.coupon.feign.SettlementClient;
import com.ron.coupon.feign.TemplateClient;
import com.ron.coupon.service.IRedisService;
import com.ron.coupon.service.IUserService;
import com.ron.coupon.vo.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of User service interface
 * All the operations and status are stored in Redis, Asynchronously handled by Kafka to pass it to MySQL
 * 所有的操作过程和状态都保存在REDIS中，通过KAFKA传递到MYSQL中做异步处理
 */
@Service
@Slf4j
public class UserServiceImpl implements IUserService {
    private final CouponDAO couponDAO;
    private final IRedisService redisService;
    private final TemplateClient templateClient;
    private final SettlementClient settlementClient;
    /** Kafka Client 客户端 */
    private final KafkaTemplate<String, String> kafkaTemplate;

    public UserServiceImpl(CouponDAO couponDAO, IRedisService redisService,
                           TemplateClient templateClient, SettlementClient settlementClient,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.couponDAO = couponDAO;
        this.redisService = redisService;
        this.templateClient = templateClient;
        this.settlementClient = settlementClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException {
        List<Coupon> curCached = redisService.getCachedCoupons(userId, status);
        List<Coupon> preTarget;
        if(CollectionUtils.isNotEmpty(curCached)){
            log.debug("Coupon cache not empty, userId: {}, status: {}", userId, status);
            preTarget = curCached;
        } else {
            log.debug("Coupon cache is Empty, let's get Coupon from DB," +
                    " userId: {}, status: {}", userId, status);
            List<Coupon> dbCoupons = couponDAO.findAllByUserIdAndStatus(
                    userId, CouponStatus.of(status));

            // If no record in DB, we can directly return,
            // as getCachedCoupons() have already insert a invalid coupon in Cache.
            if(CollectionUtils.isEmpty(dbCoupons)){
                log.debug("Current user doesn't have any coupon," +
                        " userId: {}, status: {}", userId, status);
                return dbCoupons;
            }
            // Not empty, we have to fill templateSDK of dbCoupons
            Map<Integer, CouponTemplateSDK> id2TemplateSDK =
                    templateClient.findIds2TemplateSDK(
                            dbCoupons.stream()
                                    .map(Coupon::getTemplateId)
                            .collect(Collectors.toList())
                    ).getData();
            dbCoupons.forEach(dc ->
                    dc.setTemplateSDK(id2TemplateSDK.get(dc.getTemplateId())));
            // There indeed is record in DB.
            preTarget = dbCoupons;
            // Save the record to Cache
            redisService.addCouponToCache(userId, preTarget, status);
        }
        // Get rid of invalid coupon with id of -1
        preTarget = preTarget.stream().filter(c -> c.getId() != -1)
                .collect(Collectors.toList());
        // If we are getting USABLE coupons, handle of Expired Coupons(Lazy Handling)
        // 如果当前获取的是可用优惠券，要做对已过期优惠券对延迟处理
        if(CouponStatus.of(status) == CouponStatus.USABLE){
            CouponClassify classify = CouponClassify.classify(preTarget);
            /// If there is expired coupons, handle them
            if(CollectionUtils.isNotEmpty(classify.getExpiredCoupons())){
                log.info("Add exired coupons to cache from FindCouponByStatus():" +
                        " userId: {}, status: {}", userId, status);
                redisService.addCouponToCache(userId,
                        classify.getExpiredCoupons(), CouponStatus.EXPIRED.getCode());
                // Send to Kafka for Asynchronous Handling in DB.
                kafkaTemplate.send(
                        Constant.TOPIC,
                        JSON.toJSONString(new CouponKafkaMessage(
                                CouponStatus.EXPIRED.getCode(),
                                classify.getExpiredCoupons().stream()
                                .map(Coupon::getId).collect(Collectors.toList())
                        ))
                );
            }
            return classify.getUsableCoupons();
        }
        return preTarget;
    }

    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {
        long curTime = new Date().getTime();
        List<CouponTemplateSDK> templateSDKs = templateClient.findAllUsableTemplate().getData();
        log.debug("Find All Template(From TemplateClient) Count: {}", templateSDKs.size());
        //Filter Expired Coupon Templates
        templateSDKs.stream().filter(
                t -> t.getRule().getExpiration().getDeadline() > curTime
        ).collect(Collectors.toList());
        log.info("Find Usable Template Count: {}", templateSDKs.size());

        // key: templateId, value: Pair
        // In Pair: left: Template limitation, right: template Info
        Map<Integer, Pair<Integer, CouponTemplateSDK>> limit2Template = new HashMap<>(templateSDKs.size());
        templateSDKs.forEach(
                t -> limit2Template.put(
                        t.getId(), Pair.of(t.getRule().getLimitation(), t)
                ));
        List<CouponTemplateSDK> result = new ArrayList<>(limit2Template.size());
        List<Coupon> userUsableCoupons = findCouponsByStatus(userId, CouponStatus.USABLE.getCode());
        log.debug("Current user has Usable Coupons: userId: {}, count: {}", userId, userUsableCoupons.size());
        // key: templateId
        Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons.stream().collect(
                Collectors.groupingBy(Coupon::getTemplateId));
        // Judge if current user can adopt template according to template Rule
        limit2Template.forEach((k,v) -> {
            int limitation = v.getLeft();
            CouponTemplateSDK templateSDK = v.getRight();
            /// Exceeds the limitation of coupons a user can adopt
            if(templateId2Coupons.containsKey(k) && templateId2Coupons.get(k).size() >= limitation){
                return;
            }
            result.add(templateSDK);
        });
        return result;
    }

    /**
     * 1. Get Corresponding coupons from Template Client, and check expiration
     * 2. judge if user can adopt according to limitation
     * 3. save to DB
     * 4. Fill CouponTemplateSDK
     * 5. Save to Cache
     * @param request {@link AcquireTemplateRequest}
     * @return {@link Coupon}
     * @throws CouponException
     */
    @Override
    public Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException {
        Map<Integer, CouponTemplateSDK> id2Template = templateClient.findIds2TemplateSDK(
                Collections.singletonList(request.getTemplateSDK().getId())
        ).getData();
        /// Validation of Coupon Template
        if(id2Template.size() <= 0){
            log.debug("Can not acquire Template from TemplateClient: " +
                    "templateId: {}", request.getTemplateSDK().getId());
            throw new CouponException("Can not acquire Template from TemplateClient");
        }
        // Can user adopt this coupon?
        List<Coupon> userUsableCoupons = findCouponsByStatus(
                request.getUserId(), CouponStatus.USABLE.getCode());
        Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons.stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));
        if(templateId2Coupons.containsKey(request.getTemplateSDK().getId()) &&
         templateId2Coupons.get(request.getTemplateSDK().getId()).size() >=
                 request.getTemplateSDK().getRule().getLimitation()){
            log.error("Exceeds the assign limitation: templateId: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Exceeds the assign limitation");
        }
        // Try to acquire Coupon Code
        String couponCode = redisService.tryToAcquireCouponCodeFromCache(
                request.getTemplateSDK().getId());
        if(StringUtils.isEmpty(couponCode)){
            log.error("Can not acquire Coupon code because All coupons are assigned: templateId: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Can not acquire Coupon code because All coupons are assigned");
        }
        // User acquire the Coupon
        Coupon newCoupon = new Coupon(request.getTemplateSDK().getId(), request.getUserId(),
                couponCode, CouponStatus.USABLE);
        newCoupon = couponDAO.save(newCoupon);

        // Before saving it to Cache, we must Fill the CouponTemplateSDK of newCoupon
        newCoupon.setTemplateSDK(request.getTemplateSDK());
        // Save to cache
        redisService.addCouponToCache(request.getUserId(), Collections.singletonList(newCoupon),
                CouponStatus.USABLE.getCode());
        return newCoupon;
    }

    /**
     * Settlement of Coupon 结算（核销）优惠券
     * Rule related Process is handled by Settlement service 规则相关的处理由Settlement服务去做
     * Only verification process is handled in current(Distribution) Service
     * 当前服务仅仅做业务处理过程（校验过程）
     * @param info {@link SettlementInfo}
     * @return
     * @throws CouponException
     */
    @Override
    public SettlementInfo settlement(SettlementInfo info) throws CouponException {
        // When no coupon, directly return original price
        List<SettlementInfo.CouponAndTemplateInfo> ctInfos = info.getCouponAndTemplateInfos();
        if(CollectionUtils.isEmpty(ctInfos)){
            log.info("Empty coupons for settlement.");
            double goodsSum = 0.0;
            for (GoodsInfo goodsInfo : info.getGoodsInfos()) {
                goodsSum += goodsInfo.getPrice() * goodsInfo.getCount();
            }
            // No coupon, don't have to set other attributes in SettlementInfo
            info.setCost(retain2Decimals(goodsSum));
        }

        // Verify if the passed in Coupons belongs to user
        List<Coupon> userCoupons = findCouponsByStatus(
                info.getUserId(), CouponStatus.USABLE.getCode()
        );
        Map<Integer, Coupon> couponId2Coupon = userCoupons.stream()
                .collect(Collectors.toMap(
                        Coupon::getId,
                        Function.identity()
                ));
        if(MapUtils.isEmpty(couponId2Coupon) || !CollectionUtils.isSubCollection(
                ctInfos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId)
                .collect(Collectors.toList()), couponId2Coupon.keySet()
        )){
            log.info("Current user's coupon: {}", couponId2Coupon.keySet());
            log.info("Passed in coupons: {}", ctInfos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId)
                    .collect(Collectors.toList()));
            log.error("The passed in coupons not belong to current user!");
            throw new CouponException("The passed in coupons not belong to current user!");
        }
        log.debug("The passed in coupons belongs to current user");
        List<Coupon> settleCoupons = new ArrayList<>(ctInfos.size());
        ctInfos.forEach(ci -> settleCoupons.add(couponId2Coupon.get(ci.getId())));

        // TODO: Finish Settlement Service for computing the Rules
        SettlementInfo processedInfo = settlementClient.computeRule(info).getData();
        if(processedInfo.getEmploy() && CollectionUtils.isNotEmpty(processedInfo.getCouponAndTemplateInfos())){
            log.info("Settlement User Coupon: userId: {}, settleCoupons: {}",
                    info.getUserId(), JSON.toJSONString(settleCoupons));
            /// Update Cache
            redisService.addCouponToCache(
                    info.getUserId(),
                    settleCoupons,
                    CouponStatus.USED.getCode()
            );
            /// Update DB
            kafkaTemplate.send(
                    Constant.TOPIC,
                    JSON.toJSONString(new CouponKafkaMessage(
                            CouponStatus.USED.getCode(),
                            settleCoupons.stream().map(Coupon::getId).collect(Collectors.toList())
                    ))
            );
        }

        return processedInfo;
    }

    // Keep two digits after decimal point
    private double retain2Decimals(double value){
        // BigDecimal.ROUND_HALF_UP: 四舍五入
        return new BigDecimal(value)
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();

    }
}
