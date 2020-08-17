package com.ron.coupon.vo;

import com.ron.coupon.constant.CouponStatus;
import com.ron.coupon.constant.PeriodType;
import com.ron.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.time.DateUtils;

import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classify coupon of users according to coupon status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponClassify {
    private List<Coupon> usableCoupons;
    private List<Coupon> usedCoupons;
    private List<Coupon> expiredCoupons;
    /** Classify the coupons get from DB
     * @param coupons coupons of a user that we get from DB
     */
    public static CouponClassify classify(List<Coupon> coupons){
        List<Coupon> usable = new ArrayList<>(coupons.size());
        List<Coupon> used = new ArrayList<>(coupons.size());
        List<Coupon> expired = new ArrayList<>(coupons.size());
        coupons.forEach(c -> {
            boolean isTimeExpired;
            long curTime = new Date().getTime();
            if(c.getTemplateSDK().getRule().getExpiration().getPeriod().equals(
                    PeriodType.REGULAR.getCode()
            )){
                isTimeExpired = c.getTemplateSDK().getRule().getExpiration()
                        .getDeadline() <= curTime;
            } else { // valid for x days from distributed date 比如从领取日期起7日内
                isTimeExpired = DateUtils.addDays(
                        c.getAssignTime(),
                        c.getTemplateSDK().getRule().getExpiration().getGap()
                ).getTime() <= curTime;
            }
            if(c.getStatus() == CouponStatus.USED){
                used.add(c);
            }else if(c.getStatus() == CouponStatus.EXPIRED || isTimeExpired){
                expired.add(c);
            }else{
                usable.add(c);
            }
        });
        return new CouponClassify(usable, used, expired);
    }
}
