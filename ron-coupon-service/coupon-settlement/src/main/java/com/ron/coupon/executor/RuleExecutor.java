package com.ron.coupon.executor;

import com.ron.coupon.constant.RuleFlag;
import com.ron.coupon.vo.SettlementInfo;

/**
 * Interface definition for Executing rules
 */
public interface RuleExecutor {
    /**
     * Config of rule types
     * @return {@link RuleFlag}
     */
    RuleFlag ruleConfig();

    /**
     * Calculation of Rules applied
     * @param settlementInfo {@link SettlementInfo} Includes the coupons selected
     * @return {@link SettlementInfo} Modified Settlement Result
     */
    SettlementInfo computeRule(SettlementInfo settlementInfo);
}
