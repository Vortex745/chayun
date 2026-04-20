package com.xu.tea.entity;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Message {
    private Integer id;
    private String content;
    private Date createTime;
    private Integer userId;
    private String username;
    private String phone;
    private String state; // "未读", "已读"
    private String reply;
    private List<Reply> replyList;
}