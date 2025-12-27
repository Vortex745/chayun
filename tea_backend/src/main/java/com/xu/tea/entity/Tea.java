package com.xu.tea.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 茶叶实体类
 * 对应数据库表：t_tea
 */
@Data
public class Tea {
    // 主键 ID
    private Integer id;

    // 茶叶名称
    private String name;

    // 价格 (涉及金额建议用 BigDecimal，比 Double 更精准)
    private BigDecimal price;

    // 库存
    private Integer stock;

    // 简介
    private String content;

    // 图片地址 (对应数据库 img_url，MyBatis 配置开启了驼峰转换，会自动匹配)
    private String imgUrl;

    // 上架时间
    private Date createTime;
}