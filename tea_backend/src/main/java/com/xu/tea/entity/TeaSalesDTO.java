package com.xu.tea.entity;

import lombok.Data;

/**
 * 专门用于通过 SQL 查询出来的统计结果
 */
@Data
public class TeaSalesDTO {
    private String name;  // 对应 tea_name
    private Integer value; // 对应 sum(count)
}