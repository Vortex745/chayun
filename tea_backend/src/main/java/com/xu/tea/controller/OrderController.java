package com.xu.tea.controller;

import com.xu.tea.common.Result;
import com.xu.tea.entity.Cart;
import com.xu.tea.entity.Order;
import com.xu.tea.mapper.CartMapper;
import com.xu.tea.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.xu.tea.entity.TeaSalesDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CartMapper cartMapper;
    // 1. 列表
    @GetMapping("/list")
    public Result<List<Order>> list() {
        return Result.success(orderMapper.selectAll());
    }

    // 2. 发货 (把状态改为 1)
    @PostMapping("/ship/{id}")
    public Result<?> ship(@PathVariable Integer id) {
        orderMapper.updateStatus(id, 1); // 1 代表已发货
        return Result.success("发货成功");
    }

    // 3. 删除
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        orderMapper.deleteById(id);
        return Result.success("删除成功");
    }
    @GetMapping("/stats")
    public Result<List<TeaSalesDTO>> getStats() {
        return Result.success(orderMapper.selectTeaSales());
    }
    // 4. 用户下单接口
    // 参数接收：userId, consignee, phone, address
    @PostMapping("/add")
    public Result<?> addOrder(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get("userId");
        String consignee = (String) params.get("consignee");
        String phone = (String) params.get("phone");
        String address = (String) params.get("address");

        // 1. 查出该用户的购物车
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        if (cartList == null || cartList.isEmpty()) {
            return Result.error("购物车为空");
        }

        // 2. 遍历购物车，生成订单
        for (Cart cart : cartList) {
            Order order = new Order();
            order.setTeaName(cart.getTeaName());
            order.setImgUrl(cart.getImgUrl());
            order.setCount(cart.getCount());
            // 计算总价：单价 * 数量
            order.setTotalPrice(cart.getPrice().multiply(new BigDecimal(cart.getCount())));
            order.setStatus(0); // 0 代表待发货
            order.setConsignee(consignee);
            order.setPhone(phone);
            order.setAddress(address);

            // 插入订单表
            orderMapper.insert(order);

            // 3. 从购物车删除该商品
            cartMapper.deleteById(cart.getId());
        }

        return Result.success("下单成功");
    }
    @PostMapping("/send")
    public Result<?> send(@RequestBody Order order) {
        orderMapper.sendOrder(order);
        return Result.success("发货成功");
    }
}