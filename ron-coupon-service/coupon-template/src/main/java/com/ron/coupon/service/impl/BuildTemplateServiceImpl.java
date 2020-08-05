package com.ron.coupon.service.impl;

import com.ron.coupon.dao.CouponTemplateDao;
import com.ron.coupon.entity.CouponTemplate;
import com.ron.coupon.exception.CouponException;
import com.ron.coupon.service.IAsyncService;
import com.ron.coupon.service.IBuildTemplateService;
import com.ron.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Build Template Service Implementation
 */
@Slf4j
@Service
public class BuildTemplateServiceImpl implements IBuildTemplateService {
    @Autowired
    private IAsyncService asyncService;

    @Autowired
    private CouponTemplateDao templateDao;

    @Override
    public CouponTemplate buildTemplate(TemplateRequest request) throws CouponException {
        if(!request.validate()){
            throw new CouponException("BuildTemplate Param Is Not Valid");
            ///TODO: Could notify which param not valid
        }
        /** Not null, so exist template with same name 不为空说明存在同名的优惠券模版*/
        if(null != templateDao.findByName(request.getName())){
            throw new CouponException("Exist Same Name Template!");
        }
        /** Build template and save to DB */
        CouponTemplate template = requestToTemplate(request);
        template = templateDao.save(template);
        /** Asynchronously create coupon code */
        asyncService.asyncConstructCouponByTemplate(template);

        return template;
    }

    private CouponTemplate requestToTemplate(TemplateRequest request){
        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule()
        );
    }
}
