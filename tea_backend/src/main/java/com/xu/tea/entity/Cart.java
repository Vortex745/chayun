package com.xu.tea.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Cart {
    private Integer id;
    private Integer userId;
    private Integer teaId;
    private Integer count;
    private Date createTime;

    // === 下面这些字段是数据库 t_cart 表里没有的 ===
    // === 我们通过 SQL 关联查询 t_tea 表填进来，方便前端显示 ===
    private String teaName;
    private String imgUrl;
    private BigDecimal price;
}