package com.ron.coupon.executor;

import com.ron.coupon.constant.CouponCategory;
import com.ron.coupon.constant.RuleFlag;
import com.ron.coupon.exception.CouponException;
import com.ron.coupon.vo.SettlementInfo;
import com.sun.javaws.exceptions.CouldNotLoadArgumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager for executing coupons
 * Find corresponding Executor to User request({@link com.ron.coupon.vo.SettlementInfo}
 * 根据用户的请求（SettlementInfo 找到对应的Executor去结算
 * BeanPostProcessor: Will be executed after all beans are handled by Container
 * 后置处理器, 在Bean都被容器创建完之后执行
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class ExecutorManager implements BeanPostProcessor {
    /** Mapping between RuleFlag and corresponding Executor */
    private static Map<RuleFlag, RuleExecutor> executorIndex = new HashMap<>(RuleFlag.values().length);

    /** Before initialization of Bean */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(!(bean instanceof RuleExecutor)){
            return bean;
        }
        RuleExecutor executor = (RuleExecutor) bean;
        if(executorIndex.containsKey(executor)){
            log.debug("Current executor already been registered for rule flag: {}",
                    executor.ruleConfig());
        }
        log.info("Load executor: {} for rule flag: {}", executor, executor.ruleConfig());
        executorIndex.put(executor.ruleConfig(), executor);
        return null;
    }

    /** After initialization of Bean */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /** Entry for Coupon compute rule
     *  NOTICE: Must ensure the input coupon number >= 1
     * */
    public SettlementInfo computeRule(SettlementInfo settlementInfo) throws CouponException {
        SettlementInfo result = null;
        if(settlementInfo.getCouponAndTemplateInfos().size() == 1){
            CouponCategory category = CouponCategory.of(
                    settlementInfo.getCouponAndTemplateInfos().get(0).getTemplate().getCategory()
            );
            switch (category){
                case PERCENT_OFF:
                    result = executorIndex.get(RuleFlag.PERCENT_OFF)
                            .computeRule(settlementInfo);
                    break;
                case DEDUCT_X_OFF_Y:
                    result = executorIndex.get(RuleFlag.DEDUCT_X_OFF_Y)
                            .computeRule(settlementInfo);
                    break;
                case DIRECT_DEDUCT:
                    result = executorIndex.get(RuleFlag.DIRECT_DEDUCT)
                            .computeRule(settlementInfo);
                    break;
            }
        }else{
            List<CouponCategory> categories = new ArrayList<>();
            settlementInfo.getCouponAndTemplateInfos().forEach(
                    ct -> {categories.add(CouponCategory.of(
                                ct.getTemplate().getCategory()
                        ));
                    }
            );
            // TODO: Can modify if we are going to allow more than 2 coupons applied
            if(categories.size() > 2){
                throw new CouponException("Can not apply more than 2 Coupons!");
            }else{

                if(categories.contains(CouponCategory.DEDUCT_X_OFF_Y) &&
                categories.contains(CouponCategory.PERCENT_OFF)){
                    result = executorIndex.get(RuleFlag.DISCOUNT_AND_PERCENT).computeRule(settlementInfo);
                }
                else{
                    // TODO: Can add more executors for other combinations
                    throw new CouponException("Currently not supporting other combination of Coupons");
                }
            }

        }
        return result;
    }

}
