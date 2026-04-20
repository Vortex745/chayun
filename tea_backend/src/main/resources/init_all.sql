-- 茶云项目全库初始化脚本
-- 数据库: tea_mall

CREATE DATABASE IF NOT EXISTS tea_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE tea_mall;

-- 1. 管理员表
DROP TABLE IF EXISTS t_admin;
CREATE TABLE t_admin (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 插入默认管理员: admin / 123456
INSERT INTO t_admin (username, password, nickname) VALUES ('admin', '123456', '超级管理员');

-- 2. 用户表
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    phone VARCHAR(20) COMMENT '电话',
    address VARCHAR(200) COMMENT '地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 3. 茶叶商品表
DROP TABLE IF EXISTS t_tea;
CREATE TABLE t_tea (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    price DECIMAL(10, 2) NOT NULL COMMENT '价格',
    stock INT DEFAULT 0 COMMENT '库存',
    content TEXT COMMENT '简介/详情',
    img_url VARCHAR(500) COMMENT '图片地址',
    type VARCHAR(50) COMMENT '分类',
    click_count INT DEFAULT 0 COMMENT '点击量',
    specs VARCHAR(255) COMMENT '规格',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='茶叶商品表';

-- 4. 轮播图表
DROP TABLE IF EXISTS t_carousel;
CREATE TABLE t_carousel (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    img_url VARCHAR(500) NOT NULL COMMENT '图片地址',
    link_text VARCHAR(255) COMMENT '广告语',
    sort INT DEFAULT 0 COMMENT '排序',
    is_show INT DEFAULT 1 COMMENT '1-展示 0-隐藏',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- 5. 购物车表
DROP TABLE IF EXISTS t_cart;
CREATE TABLE t_cart (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id INT NOT NULL COMMENT '用户ID',
    tea_id INT NOT NULL COMMENT '商品ID',
    count INT DEFAULT 1 COMMENT '数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 6. 留言表
DROP TABLE IF EXISTS t_message;
CREATE TABLE t_message (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    content TEXT NOT NULL COMMENT '留言内容',
    userId INT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    phone VARCHAR(20) COMMENT '联系电话',
    state VARCHAR(20) DEFAULT '未读' COMMENT '状态: 未读/已读',
    reply TEXT COMMENT '回复内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言表';

-- 7. 回复表 (与留言关联)
DROP TABLE IF EXISTS t_reply;
CREATE TABLE t_reply (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    message_id INT NOT NULL COMMENT '留言ID',
    content TEXT NOT NULL COMMENT '回复内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '回复时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回复表';

-- 8. 订单表
DROP TABLE IF EXISTS t_order;
CREATE TABLE t_order (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    user_id INT COMMENT '用户ID',
    tea_name VARCHAR(100) COMMENT '茶叶名称',
    img_url VARCHAR(500) COMMENT '商品图片',
    count INT COMMENT '购买数量',
    total_price DECIMAL(10, 2) COMMENT '总价',
    status INT DEFAULT 0 COMMENT '订单状态: 0待发货 1已发货 2已完成',
    state VARCHAR(20) DEFAULT '待发货' COMMENT '订单状态文字',
    consignee VARCHAR(50) COMMENT '收货人',
    phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(200) COMMENT '收货地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    order_no VARCHAR(50) COMMENT '订单号',
    username VARCHAR(50) COMMENT '用户名',
    delivery_method VARCHAR(20) COMMENT '配送方式',
    courier_company VARCHAR(50) COMMENT '快递公司',
    tracking_number VARCHAR(100) COMMENT '快递单号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 插入一些初始化数据
INSERT INTO t_tea (name, price, stock, content, type, img_url) VALUES 
('西湖龙井', 299.00, 100, '特级西湖龙井，清香耐泡。', '绿茶', 'https://images.unsplash.com/photo-1544787210-2827255ee972'),
('安溪铁观音', 199.00, 200, '浓郁兰花香，回甘无穷。', '乌龙茶', 'https://images.unsplash.com/photo-1515696955266-4f67e13219e8'),
('武夷大红袍', 599.00, 50, '岩韵深厚，名品好茶。', '红茶', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9');

INSERT INTO t_carousel (img_url, link_text, sort) VALUES 
('https://images.unsplash.com/photo-1544787210-2827255ee972', '春季新茶上市', 1),
('https://images.unsplash.com/photo-1515696955266-4f67e13219e8', '品味经典铁观音', 2);
