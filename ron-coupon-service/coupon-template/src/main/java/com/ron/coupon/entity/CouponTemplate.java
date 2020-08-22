package com.ron.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ron.coupon.constant.CouponCategory;
import com.ron.coupon.constant.DistributeTarget;
import com.ron.coupon.constant.ProductLine;
import com.ron.coupon.converter.CouponCategoryConverter;
import com.ron.coupon.converter.DistributeTargetConverter;
import com.ron.coupon.converter.ProductLineConverter;
import com.ron.coupon.converter.RuleConverter;
import com.ron.coupon.serialization.CouponTemplateSerialize;
import com.ron.coupon.vo.TemplateRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Coupon template entity class definition: basic property + Rule property
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon_template")
@JsonSerialize(using = CouponTemplateSerialize.class)
public class CouponTemplate implements Serializable {

    /** Auto-incremented primary key 自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Basic
//    @Transient //just for showing the function of Transient
    private Integer id;

    /** Valid or not 是否是可用状态 */
    @Column(name = "available", nullable = false)
    private Boolean available;

    /** Expired or not */
    @Column(name = "expired", nullable = false)
    private Boolean expired;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "logo", nullable = false)
    private String logo;

    @Column(name = "intro", nullable = false)
    private String desc;

    @Column(name = "category", nullable = false)
    @Convert(converter = CouponCategoryConverter.class)
    private CouponCategory category;

    @Column(name = "product_line", nullable = false)
    @Convert(converter = ProductLineConverter.class)
    private ProductLine productLine;

    /** Could be type of BigInteger */
    @Column(name = "coupon_count", nullable = false)
    private Integer count;

    @CreatedDate
    @Column(name = "create_time", nullable = false)
    private Date createTime;

    /** The employee id who created this template of coupon. 用户ID */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "template_key", nullable = false)
    private String key;

    @Column(name = "target", nullable = false)
    @Convert(converter = DistributeTargetConverter.class)
    private DistributeTarget target;

    @Column(name = "rule", nullable = false)
    @Convert(converter = RuleConverter.class)
    private TemplateRule rule;

    public CouponTemplate(String name, String logo, String desc
            , String category, Integer productLine, Integer count
            , Long userId, Integer target, TemplateRule rule){
        this.available = false;
        this.expired = false;
        this.name = name;
        this.logo = logo;
        this.desc = desc;
        this.category = CouponCategory.of(category);
        this.productLine = ProductLine.of(productLine);
        this.count = count;
        this.userId = userId;
        /** Unique code of each coupon = 4(ProductLine + category) + 8(Date:YYYYmmDD) + 4(id) */
        this.key = productLine.toString() + category +
                new SimpleDateFormat("yyyyMMdd").format(new Date());
        this.target = DistributeTarget.of(target);
        this.rule = rule;
    }
}
