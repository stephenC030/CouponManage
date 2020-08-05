package com.ron.coupon.service.impl;

import com.ron.coupon.dao.CouponTemplateDao;
import com.ron.coupon.entity.CouponTemplate;
import com.ron.coupon.exception.CouponException;
import com.ron.coupon.service.ITemplateBaseService;
import com.ron.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Coupon Template Basic Service Implementation
 */
@Slf4j
@Service
public class TemplateBaseServiceImpl implements ITemplateBaseService {
    private final CouponTemplateDao templateDao;

    @Autowired
    public TemplateBaseServiceImpl(CouponTemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    /** Get template info with template id
     * @param id Template id
     * @return {@link CouponTemplate} Template Entity
     * */
    @Override
    public CouponTemplate buildTemplateInfo(Integer id) throws CouponException {
        Optional<CouponTemplate> template = templateDao.findById(id);
        if(!template.isPresent()){
            throw new CouponException("Template Is Not Exist: " + id);
        }
        return template.get();
    }

    /**
     * Find all available template
     * @return {@link CouponTemplateSDK}s
     */
    @Override
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        List<CouponTemplate> templates =
                templateDao.findAllByAvailableAndExpired(true, false);

        return templates.stream().map(this::template2templateSDK).collect(Collectors.toList());
    }

    /**
     * Get Mapping of template ids to CouponTemplateSDK 获取模版到SDK的映射
     * @param ids template ids
     * @return Map<key: template id, value: CouponTemplateSDK>
     */
    @Override
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids) {
        List<CouponTemplate> templates = templateDao.findAllById(ids);
        return templates.stream().map(this::template2templateSDK).collect(Collectors.toMap(
                CouponTemplateSDK::getId, Function.identity()
        ));
    }
    /** Transform CouponTemple to CouponTemplateSDK */
    private CouponTemplateSDK template2templateSDK(CouponTemplate template){
        return new CouponTemplateSDK(
                template.getId(),
                template.getName(),
                template.getLogo(),
                template.getDesc(),
                template.getCategory().getCode(),
                template.getProductLine().getCode(),
                template.getKey(), ////Not concatenated Template Key.
                template.getTarget().getCode(),
                template.getRule()
        );
    }
}
