package com.xu.tea.mapper;

import com.xu.tea.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import java.util.List;

@Mapper
public interface UserMapper {
    // 1. 管理员用：查询列表
    @Select("SELECT * FROM t_user ORDER BY create_time DESC")
    List<User> selectAll();

    // 2. 管理员用：删除
    @Delete("DELETE FROM t_user WHERE id = #{id}")
    void deleteById(Integer id);

    // --- 新增给用户端用的 ---

    // 3. 登录/注册检查：根据用户名查询
    @Select("SELECT * FROM t_user WHERE username = #{username}")
    User selectByUsername(String username);

    // 4. 注册：插入新用户
    @Insert("INSERT INTO t_user(username, password, nickname, phone, address, create_time) " +
            "VALUES(#{username}, #{password}, #{nickname}, #{phone}, #{address}, NOW())")
    void insert(User user);
    // ... 原有代码 ...

    // 5. 更新个人信息 (动态SQL，只更新非空字段)
    // 注意：这里为了简单直接写在注解里，实际上字段多了建议用 XML
    @org.apache.ibatis.annotations.Update("<script>" +
            "UPDATE t_user SET " +
            "<if test='password != null'>password=#{password},</if>" +
            "<if test='nickname != null'>nickname=#{nickname},</if>" +
            "<if test='phone != null'>phone=#{phone},</if>" +
            "<if test='address != null'>address=#{address},</if>" +
            "id = #{id} WHERE id = #{id}" +
            "</script>")
    void update(User user);
}