package com.xu.tea.mapper;// ... 原有的 package 和 import

import com.xu.tea.entity.Tea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeaMapper {
    // 原有的查询所有
    List<Tea> selectAll();

    // 1. 新增：根据 ID 查询详情
    Tea selectById(Integer id);

    // 2. 新增：插入一条新数据
    // 注意：这里参数是 Tea 对象，MyBatis 会自动把属性填入 SQL
    void insert(Tea tea);

    // 3. 新增：根据 ID 删除
    void deleteById(Integer id);
    // 4. 新增：更新
    void update(Tea tea);

    @Select("SELECT * FROM t_tea WHERE name LIKE concat('%', #{name}, '%') ORDER BY create_time DESC")
    List<Tea> selectByName(String name);

    @Select("SELECT * FROM t_tea WHERE type = #{type} ORDER BY create_time DESC")
    List<Tea> selectByType(String type);

}