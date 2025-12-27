package com.xu.tea.mapper;
import com.xu.tea.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {
    // 根据用户名查询管理员
    @Select("SELECT * FROM t_admin WHERE username = #{username}")
    Admin selectByUsername(String username);
}