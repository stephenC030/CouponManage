package com.ron.coupon.schedule;

import com.ron.coupon.dao.CouponTemplateDao;
import com.ron.coupon.entity.CouponTemplate;
import com.ron.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Delete expired coupon template at schedule
 * 定时清理已经过期的优惠券模版
 */
@Slf4j
@Component
public class ScheduledTask {
    @Autowired
    private CouponTemplateDao templateDao;

    /** offline the expired Templates every 1 hour */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void offlineCouponTemplate(){
        log.info("Start to Expire CouponTemplate");
        List<CouponTemplate> templates = templateDao.findAllByExpired(false);
        if(CollectionUtils.isEmpty(templates)){
            log.info("Done to Expire CouponTemplate. ");
            return;
        }
        Date curDate = new Date();
        List<CouponTemplate> expiredTemplates = new ArrayList<>(templates.size());
        templates.forEach(t ->{
            /** Validate expiration according to template rule */
            TemplateRule rule = t.getRule();
            if(rule.getExpiration().getDeadline() < curDate.getTime()){
                t.setExpired(true);
                expiredTemplates.add(t);
            }
        });

        if(!CollectionUtils.isEmpty(expiredTemplates)){
            log.info("Expired CouponTemplate Num :{}",
                    templateDao.saveAll(expiredTemplates));
        }
        log.info("Done to Expire CouponTemplate. ");

    }

}
