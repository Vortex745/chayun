-- =========================================
-- 茶云商城 (tea_mall) 数据库全量初始化脚本
-- 版本: v2.0
-- 说明: 包含建库、建表、示例数据
--       可直接在 MySQL 客户端执行：source init_all.sql
-- =========================================

-- 建库
CREATE DATABASE IF NOT EXISTS tea_mall
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE tea_mall;

-- 关闭外键检查，方便按任意顺序 DROP
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================
-- 1. 管理员表 t_admin
-- =========================================
DROP TABLE IF EXISTS t_admin;
CREATE TABLE t_admin (
    id       INT          NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    username VARCHAR(50)  NOT NULL UNIQUE        COMMENT '登录账号',
    password VARCHAR(100) NOT NULL               COMMENT '登录密码（明文/加密均可）',
    nickname VARCHAR(50)                         COMMENT '显示昵称',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '管理员表';

INSERT INTO t_admin (username, password, nickname)
VALUES ('admin', '123456', '超级管理员');

-- =========================================
-- 2. 用户表 t_user
-- =========================================
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL UNIQUE        COMMENT '用户名',
    password    VARCHAR(100) NOT NULL               COMMENT '密码',
    nickname    VARCHAR(50)                         COMMENT '昵称',
    phone       VARCHAR(20)                         COMMENT '联系电话',
    address     VARCHAR(200)                        COMMENT '默认收货地址',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '用户表';

INSERT INTO t_user (username, password, nickname, phone, address)
VALUES
    ('zhangsan', '123456', '张三',   '13800138001', '北京市海淀区中关村大街1号'),
    ('lisi',     '123456', '李四',   '13800138002', '上海市浦东新区陆家嘴环路1000号'),
    ('wangwu',   '123456', '王五',   '13800138003', '广州市天河区天河路385号'),
    ('test',     '123456', '测试用户','13900000000', '浙江省杭州市西湖区文三路100号');

-- =========================================
-- 3. 茶叶商品表 t_tea
-- =========================================
DROP TABLE IF EXISTS t_tea;
CREATE TABLE t_tea (
    id          INT             NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    name        VARCHAR(100)    NOT NULL               COMMENT '商品名称',
    price       DECIMAL(10, 2)  NOT NULL               COMMENT '售价（元）',
    stock       INT             NOT NULL DEFAULT 0      COMMENT '库存数量',
    content     TEXT                                   COMMENT '商品详情/简介',
    img_url     VARCHAR(500)                           COMMENT '封面图片URL',
    type        VARCHAR(50)                            COMMENT '茶叶分类（绿茶/红茶/乌龙茶等）',
    click_count INT             NOT NULL DEFAULT 0      COMMENT '点击/浏览次数',
    specs       VARCHAR(255)                           COMMENT '规格描述',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上架时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '茶叶商品表';

INSERT INTO t_tea (name, price, stock, content, img_url, type, click_count, specs)
VALUES
    ('西湖龙井（特级）', 298.00, 120,
     '产自杭州西湖核心产区，色泽翠绿，香气清高持久，滋味鲜醇，是绿茶中的极品。每年明前采摘，只取一芽一叶。',
     'https://images.unsplash.com/photo-1544787210-2827255ee972?w=400',
     '绿茶', 523, '50g / 100g / 250g'),

    ('碧螺春（洞庭山）', 188.00, 200,
     '洞庭碧螺春产于苏州太湖洞庭山，茶条卷曲，茸毛遍布，白毫毕露，银绿隐翠，清香淡雅。',
     'https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=400',
     '绿茶', 312, '50g / 100g'),

    ('安溪铁观音（浓香型）', 168.00, 300,
     '福建安溪铁观音，采用传统炭焙工艺，香型浓郁持久，带有天然兰花香，喉韵甘甜悠长，回甘持久。',
     'https://images.unsplash.com/photo-1515696955266-4f67e13219e8?w=400',
     '乌龙茶', 891, '100g / 250g / 500g'),

    ('武夷岩茶·大红袍', 598.00, 60,
     '武夷山正岩产区大红袍，岩骨花香，滋味醇厚，香气独特，是乌龙茶中的极品，被誉为"茶中之王"。',
     'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=400',
     '乌龙茶', 1024, '100g / 250g'),

    ('云南普洱（生茶）2019', 368.00, 80,
     '云南大叶种晒青毛茶压制，2019年春茶原料，存放至今已转化出迷人的陈香，汤色金黄透亮，滋味醇正。',
     'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400',
     '普洱茶', 456, '357g 饼 / 500g 散'),

    ('正山小种（烟熏松针）', 128.00, 150,
     '福建武夷山桐木关产正山小种，采用松木熏制工艺，独具浓郁的松烟香，汤色红浓，滋味醇厚甘甜。',
     'https://images.unsplash.com/photo-1546877625-cb8c71916608?w=400',
     '红茶', 287, '100g / 250g'),

    ('白毫银针（福鼎）', 428.00, 50,
     '福鼎白茶中的极品，采摘芽头制作，满披白毫，如银似雪，汤色浅黄清澈，毫香蜜韵，清新淡雅。',
     'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400',
     '白茶', 198, '50g / 100g'),

    ('茉莉花茶（特级）', 88.00, 400,
     '采用优质绿茶为茶坯，经新鲜茉莉花多次窨制，花香浓郁持久，滋味鲜醇，是老少皆宜的日常饮品。',
     'https://images.unsplash.com/photo-1563822249366-3efb23b8e0c9?w=400',
     '花茶', 654, '100g / 250g / 500g');

-- =========================================
-- 4. 轮播图表 t_carousel
-- =========================================
DROP TABLE IF EXISTS t_carousel;
CREATE TABLE t_carousel (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
    img_url     VARCHAR(500) NOT NULL               COMMENT '图片地址',
    link_text   VARCHAR(255)                        COMMENT '广告语/标题',
    sort        INT          NOT NULL DEFAULT 0      COMMENT '排序（数字越小越靠前）',
    is_show     TINYINT      NOT NULL DEFAULT 1      COMMENT '是否展示：1-展示 0-隐藏',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '首页轮播图表';

INSERT INTO t_carousel (img_url, link_text, sort, is_show)
VALUES
    ('https://images.unsplash.com/photo-1544787210-2827255ee972?w=1200', '春茶上市・龙井飘香', 1, 1),
    ('https://images.unsplash.com/photo-1515696955266-4f67e13219e8?w=1200', '铁观音浓香·沁人心脾', 2, 1),
    ('https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=1200', '云南普洱·岁月沉香', 3, 1),
    ('https://images.unsplash.com/photo-1563822249366-3efb23b8e0c9?w=1200', '茉莉花茶·清新一夏', 4, 0);

-- =========================================
-- 5. 购物车表 t_cart
-- =========================================
DROP TABLE IF EXISTS t_cart;
CREATE TABLE t_cart (
    id          INT      NOT NULL AUTO_INCREMENT COMMENT '购物车记录ID',
    user_id     INT      NOT NULL               COMMENT '用户ID（关联 t_user.id）',
    tea_id      INT      NOT NULL               COMMENT '商品ID（关联 t_tea.id）',
    count       INT      NOT NULL DEFAULT 1      COMMENT '加购数量',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_tea (user_id, tea_id) COMMENT '同用户同商品唯一'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '购物车表';

INSERT INTO t_cart (user_id, tea_id, count)
VALUES
    (1, 1, 2),
    (1, 3, 1),
    (2, 5, 1),
    (3, 2, 3),
    (4, 4, 1);

-- =========================================
-- 6. 留言表 t_message
-- （注意：Mapper 中使用 user_id，不是 userId）
-- =========================================
DROP TABLE IF EXISTS t_message;
CREATE TABLE t_message (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '留言ID',
    content     TEXT         NOT NULL               COMMENT '留言内容',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间',
    user_id     INT                                 COMMENT '用户ID（关联 t_user.id，游客可为 NULL）',
    username    VARCHAR(50)                         COMMENT '用户名/昵称',
    phone       VARCHAR(20)                         COMMENT '联系电话',
    state       VARCHAR(20)  NOT NULL DEFAULT '未读' COMMENT '状态：未读 / 已读',
    reply       TEXT                                COMMENT '快速回复内容（冗余字段）',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '用户留言表';

INSERT INTO t_message (content, user_id, username, phone, state)
VALUES
    ('这款龙井真的很香，下次还会购买！', 1, '张三', '13800138001', '已读'),
    ('普洱茶的包装能更精美一些吗？送礼用的', 2, '李四', '13800138002', '未读'),
    ('请问大红袍有更小的试喝装吗？', 3, '王五', '13800138003', '未读'),
    ('白毫银针收到了，品质很好，五星好评！', 4, '测试用户', '13900000000', '已读');

-- =========================================
-- 7. 回复表 t_reply
-- =========================================
DROP TABLE IF EXISTS t_reply;
CREATE TABLE t_reply (
    id          INT      NOT NULL AUTO_INCREMENT COMMENT '回复ID',
    message_id  INT      NOT NULL               COMMENT '关联的留言ID（t_message.id）',
    content     TEXT     NOT NULL               COMMENT '回复内容',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '回复时间',
    PRIMARY KEY (id),
    KEY idx_message_id (message_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '留言回复表';

INSERT INTO t_reply (message_id, content)
VALUES
    (1, '感谢您的喜爱！我们的龙井均来自西湖核心产区，欢迎再次光临~'),
    (4, '非常感谢您的认可！我们会继续为您提供高品质的好茶！');

-- =========================================
-- 8. 订单表 t_order
-- =========================================
DROP TABLE IF EXISTS t_order;
CREATE TABLE t_order (
    id              INT            NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    order_no        VARCHAR(50)                            COMMENT '订单号（前端生成，建议格式：yyyyMMddHHmmss+随机4位）',
    user_id         INT                                    COMMENT '用户ID（关联 t_user.id）',
    username        VARCHAR(50)                            COMMENT '下单用户名',
    tea_name        VARCHAR(100)                           COMMENT '购买的茶叶名称',
    img_url         VARCHAR(500)                           COMMENT '商品封面图片URL',
    count           INT            NOT NULL DEFAULT 1      COMMENT '购买数量',
    total_price     DECIMAL(10, 2) NOT NULL                COMMENT '订单总金额（元）',
    status          TINYINT        NOT NULL DEFAULT 0      COMMENT '订单状态：0待发货 1已发货 2已完成',
    state           VARCHAR(20)    NOT NULL DEFAULT '待发货' COMMENT '订单状态文字（与 status 对应）',
    consignee       VARCHAR(50)                            COMMENT '收货人姓名',
    phone           VARCHAR(20)                            COMMENT '收货联系电话',
    address         VARCHAR(200)                          COMMENT '收货地址',
    delivery_method VARCHAR(20)                            COMMENT '配送方式（快递/自提等）',
    courier_company VARCHAR(50)                            COMMENT '快递公司',
    tracking_number VARCHAR(100)                           COMMENT '快递单号',
    create_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_order_no (order_no)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '订单表';

INSERT INTO t_order (order_no, user_id, username, tea_name, img_url, count, total_price, status, state, consignee, phone, address, delivery_method, courier_company, tracking_number)
VALUES
    ('20240420100001', 1, 'zhangsan', '西湖龙井（特级）',
     'https://images.unsplash.com/photo-1544787210-2827255ee972?w=400',
     2, 596.00, 2, '已完成', '张三', '13800138001', '北京市海淀区中关村大街1号',
     '快递', '顺丰速运', 'SF1234567890'),

    ('20240420100002', 2, 'lisi', '云南普洱（生茶）2019',
     'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400',
     1, 368.00, 1, '已发货', '李四', '13800138002', '上海市浦东新区陆家嘴环路1000号',
     '快递', '中通快递', 'ZT9876543210'),

    ('20240420100003', 3, 'wangwu', '安溪铁观音（浓香型）',
     'https://images.unsplash.com/photo-1515696955266-4f67e13219e8?w=400',
     3, 504.00, 0, '待发货', '王五', '13800138003', '广州市天河区天河路385号',
     '快递', NULL, NULL),

    ('20240420100004', 4, 'test', '白毫银针（福鼎）',
     'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400',
     1, 428.00, 2, '已完成', '测试用户', '13900000000', '浙江省杭州市西湖区文三路100号',
     '快递', '圆通速递', 'YT5555555555');

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- =========================================
-- 验证：查看各表记录数
-- =========================================
SELECT 't_admin'    AS `表名`, COUNT(*) AS `记录数` FROM t_admin
UNION ALL
SELECT 't_user',    COUNT(*) FROM t_user
UNION ALL
SELECT 't_tea',     COUNT(*) FROM t_tea
UNION ALL
SELECT 't_carousel',COUNT(*) FROM t_carousel
UNION ALL
SELECT 't_cart',    COUNT(*) FROM t_cart
UNION ALL
SELECT 't_message', COUNT(*) FROM t_message
UNION ALL
SELECT 't_reply',   COUNT(*) FROM t_reply
UNION ALL
SELECT 't_order',   COUNT(*) FROM t_order;
