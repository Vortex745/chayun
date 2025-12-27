package com.xu.tea.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Order {
    private Integer id;
    private String teaName;   // 茶叶名称
    private String imgUrl;    // 图片
    private Integer count;    // 数量
    private BigDecimal totalPrice; // 总价
    private Integer status;   // 0待发货 1已发货 2已完成
    private String consignee; // 收货人
    private String phone;     // 电话
    private String address;   // 地址
    private Date createTime;
}