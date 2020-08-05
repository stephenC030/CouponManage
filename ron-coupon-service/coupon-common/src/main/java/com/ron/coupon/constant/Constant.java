package com.ron.coupon.constant;

/**
 * General constants definition 通用常量定义
 */
public class Constant {
    /** Message topic for Kafka */
    public static final String TOPIC = "ron_user_coupon_op";

    /** redis key prefix */
    public static class RedisPrefix{
        public static final String COUPON_TEMPLATE = "ron_coupon_template_code_";

        public static final String USER_COUPON_USEABLE = "ron_user_coupon_usable_";

        public static final String USER_COUPON_USED = "ron_user_coupon_used_";

        public static final String USER_COUPON_EXPIRED = "ron_user_coupon_expired";
    }
}
