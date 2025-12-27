package com.xu.tea.entity;
import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Integer id;
    private String username;
    private String password; // 实际项目中密码不应返回给前端，但为了演示方便先留着
    private String nickname;
    private String phone;
    private String address;
    private Date createTime;
}