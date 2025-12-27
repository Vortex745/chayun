package com.xu.tea.mapper;

import com.xu.tea.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CartMapper {
    // 1. 查询某个用户的购物车列表 (关联查询商品信息)
    List<Cart> selectByUserId(Integer userId);

    // 2. 检查某个用户是否已经添加过某个商品 (用于判断是新增还是累加)
    Cart selectByUserIdAndTeaId(@Param("userId") Integer userId, @Param("teaId") Integer teaId);

    // 3. 新增一条记录
    void insert(Cart cart);

    // 4. 更新数量 (累加或修改)
    void updateCount(@Param("id") Integer id, @Param("count") Integer count);

    // 5. 删除购物车项
    void deleteById(Integer id);
}