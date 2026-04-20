package com.xu.tea.entity;

import lombok.Data;

@Data
public class Carousel {
    private Integer id;
    private String imgUrl;
    private String linkText; // 广告语
    private Integer sort;    // 排序
    private Integer isShow;  // 1-展示 0-隐藏
    private String createTime;
}