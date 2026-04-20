package com.xu.tea.mapper;

import com.xu.tea.entity.Order;
import com.xu.tea.entity.TeaSalesDTO;
import org.apache.ibatis.annotations.*;

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

        // 4. 新增订单 (支持关联用户和状态)
        @Insert("INSERT INTO t_order (user_id, tea_name, img_url, count, total_price, status, state, consignee, phone, address, create_time, order_no) "
                        + "VALUES (#{userId}, #{teaName}, #{imgUrl}, #{count}, #{totalPrice}, #{status}, #{state}, #{consignee}, #{phone}, #{address}, NOW(), #{orderNo})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        void insert(Order order);

        @Update("UPDATE t_order SET state = '已发货', delivery_method = #{deliveryMethod}, courier_company = #{courierCompany}, tracking_number = #{trackingNumber} WHERE id = #{id}")
        void sendOrder(Order order);

        // 5. 立即购买订单插入 (与 insert 保持一致)
        @Insert("INSERT INTO t_order (user_id, tea_name, img_url, count, total_price, status, state, consignee, phone, address, create_time, order_no) "
                        + "VALUES (#{userId}, #{teaName}, #{imgUrl}, #{count}, #{totalPrice}, #{status}, #{state}, #{consignee}, #{phone}, #{address}, NOW(), #{orderNo})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        void insertBuyNow(Order order);
}