package com.xu.tea.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Reply {
    private Integer id;
    private Integer messageId; // 关联的留言ID
    private String content;    // 回复内容
    private Date createTime;   // 回复时间
}