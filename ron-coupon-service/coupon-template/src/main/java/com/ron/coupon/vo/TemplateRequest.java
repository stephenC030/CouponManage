package com.ron.coupon.vo;

import com.ron.coupon.constant.CouponCategory;
import com.ron.coupon.constant.DistributeTarget;
import com.ron.coupon.constant.ProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * Coupon Template creation request Object. 优惠券模版创建请求对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRequest {

    private String name;
    private String logo;
    private String desc;
    /** The code of corresponding category */
    private String category;
    private Integer productLine;
    private Integer count;
    private Long userId;
    private Integer target;
    /** Has corresponding properties */
    private TemplateRule rule;

    /** Basic Validation of the object */
    public boolean validate(){
        boolean stringValid = !StringUtils.isEmpty(name)
                && !StringUtils.isEmpty(logo)
                && !StringUtils.isEmpty(desc);
        boolean enumValid =
                null != CouponCategory.of(category)
                && null != ProductLine.of(productLine)
                && null != DistributeTarget.of(target);
        boolean numValid = count > 0 && userId > 0;
        return stringValid && enumValid && numValid && rule.validate();
    }
}
