package com.ron.coupon.vo;

import com.ron.coupon.constant.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * The rule object for coupon rule 优惠券规则对象定义
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRule {
    private Expiration expiration;

    private Discount discount;
    /** max number of coupon a user can have */
    private Integer limitation;
    /** Usage : location + goods type */
    private Usage usage;
    /** Weight(Could be applied with what other types of coupons).
     *  No multiple coupons with same type
     *  list[]: Unique code for coupons.
     *  * */
    private String weight;

    public boolean validate(){
        return expiration.validate() && discount.validate() && limitation > 0 && usage.validate()
                && !StringUtils.isEmpty(weight);
    }

    /**
     * Inner class. Valid period rule. 有效期规则
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Expiration {
        /** Rule. Correspond to PeriodType.code 有效期规则*/
        private Integer period;

        /** Valid Interval. Only applicable for SHIFT type period. 有效间隔：只对变动型有效期有效*/
        private Integer gap;

        /** expire date. Apply to both types of coupon. 失效日期，两类都有效 */
        private Long deadline;

        boolean validate(){
            //TODO: Can be improved to more complex validation
            return null != PeriodType.of(period) && gap > 0 && deadline > 0;
        }
    }

    /**
     * Discount class. Depends on type of coupon. 折扣，与优惠券类型配合决定
     * For quota variable, $quota off original price for Discount Coupon and Direct Deduct coupon
     * %quota off for Percentage Coupon, so final price = original price * quota * 1.0 / 100.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Discount{
        /** Value of discount. 额度：满减（20），折扣（85），立减（10）*/
        private Integer quota;

        /** Only applicable for DEDUCT X OFF Y type coupon. Value of Y. 基准，需要满多少才可用*/
        private Integer base;

        boolean validate(){
            return quota > 0 && base > 0;
        }

    }

    /**
     * Usage range. The coupon can be used only in x state of xx city or certain type of product.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Usage{
        private String state;
        private String city;
        /** Product type. list[Entertainment / Fresh Food / Furniture / ...] a list of JSON. */
        private String goodsType;

        public boolean validate(){
            return !StringUtils.isEmpty(state) && !StringUtils.isEmpty(city) && !StringUtils.isEmpty(goodsType);
        }
    }
}
