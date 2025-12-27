package com.xu.tea.mapper;

import com.xu.tea.entity.Order;
import com.xu.tea.entity.TeaSalesDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    // 1. 查询所有订单
    List<Order> selectAll();

    // 2. 修改订单状态 (发货/取消)
    void updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    // 3. 删除订单
    void deleteById(Integer id);
    // 4. 查询用户订单
    @Select("SELECT tea_name as name, SUM(count) as value FROM t_order GROUP BY tea_name")
    List<TeaSalesDTO> selectTeaSales();
    // 4. 新增订单
    @Insert("INSERT INTO t_order (tea_name, img_url, count, total_price, status, consignee, phone, address, create_time) " +
            "VALUES (#{teaName}, #{imgUrl}, #{count}, #{totalPrice}, #{status}, #{consignee}, #{phone}, #{address}, NOW())")
    void insert(Order order);
}