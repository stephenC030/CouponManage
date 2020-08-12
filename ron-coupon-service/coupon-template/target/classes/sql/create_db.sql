-- 登录 MySQL 服务器
mysql -hlocalhost -uroot -p12345678

-- 创建数据库 ron_coupon_data
CREATE DATABASE IF NOT EXISTS ron_coupon_data;

-- 登录 MySQL 服务器, 并进入到 ron_coupon_data 数据库中
mysql -hlocalhost -uroot -p1234 -Dron_coupon_data
