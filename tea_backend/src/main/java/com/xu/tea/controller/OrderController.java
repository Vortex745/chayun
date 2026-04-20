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
import java.util.ArrayList;
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
    @Autowired
    private com.xu.tea.mapper.TeaMapper teaMapper;

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
            // 扣减库存
            int updated = teaMapper.updateStore(cart.getTeaId(), cart.getCount());
            if (updated <= 0) {
                return Result.error("商品 " + cart.getTeaName() + " 库存不足");
            }

            Order order = new Order();
            order.setUserId(userId);
            order.setTeaName(cart.getTeaName());
            order.setImgUrl(cart.getImgUrl());
            order.setCount(cart.getCount());
            // 计算总价：单价 * 数量
            order.setTotalPrice(cart.getPrice().multiply(new BigDecimal(cart.getCount())));
            order.setStatus(0); // 0 代表待发货
            order.setState("待发货");
            order.setConsignee(consignee);
            order.setPhone(phone);
            order.setAddress(address);

            // 生成订单编号: 时间戳 + 4位随机数
            String orderNo = System.currentTimeMillis() + "" + (int) (Math.random() * 9000 + 1000);
            order.setOrderNo(orderNo);

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

    // 5. 立即购买接口 (不通过购物车，直接下单)
    @PostMapping("/buyNow")
    public Result<?> buyNow(@RequestBody Map<String, Object> params) {
        try {
            Result<?> validationError = validateOrderItem(params);
            if (validationError != null) {
                return validationError;
            }
            createOrder(params, false);
            return Result.success("下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("下单失败：" + e.getMessage());
        }
    }

    @PostMapping("/checkout")
    public Result<?> checkout(@RequestBody Map<String, Object> params) {
        try {
            Object itemsObj = params.get("items");
            if (!(itemsObj instanceof List<?> rawItems) || rawItems.isEmpty()) {
                return Result.error("请选择要结算的商品");
            }

            List<Map<String, Object>> items = new ArrayList<>();
            for (Object itemObj : rawItems) {
                if (!(itemObj instanceof Map<?, ?> rawItem)) {
                    return Result.error("订单数据格式不正确");
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> item = (Map<String, Object>) rawItem;
                Result<?> validationError = validateOrderItem(item);
                if (validationError != null) {
                    return validationError;
                }
                items.add(item);
            }

            for (Map<String, Object> item : items) {
                createOrder(item, true);
            }

            return Result.success("下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("批量下单失败：" + e.getMessage());
        }
    }

    private Result<?> validateOrderItem(Map<String, Object> params) {
        Integer teaId = toInteger(params.get("teaId"));
        Integer count = toInteger(params.get("count"));

        if (teaId == null || count == null || count <= 0) {
            return Result.error("订单参数不完整");
        }

        var tea = teaMapper.selectById(teaId);
        if (tea == null) {
            return Result.error("商品不存在");
        }
        if (tea.getStock() == null || tea.getStock() < count) {
            return Result.error("商品 " + tea.getName() + " 库存不足");
        }
        return null;
    }

    private void createOrder(Map<String, Object> params, boolean removeFromCart) {
        Integer userId = toInteger(params.get("userId"));
        Integer teaId = toInteger(params.get("teaId"));
        String teaName = (String) params.get("teaName");
        String imgUrl = (String) params.get("imgUrl");
        Integer count = toInteger(params.get("count"));
        BigDecimal price = toBigDecimal(params.get("price"));
        String consignee = (String) params.get("consignee");
        String phone = (String) params.get("phone");
        String address = (String) params.get("address");

        int updated = teaMapper.updateStore(teaId, count);
        if (updated <= 0) {
            throw new IllegalStateException("库存不足");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setTeaName(teaName);
        order.setImgUrl(imgUrl);
        order.setCount(count);
        order.setTotalPrice(price.multiply(new BigDecimal(count)));
        order.setStatus(0);
        order.setState("待发货");
        order.setConsignee(consignee);
        order.setPhone(phone);
        order.setAddress(address);
        order.setOrderNo(System.currentTimeMillis() + "" + (int) (Math.random() * 9000 + 1000));

        orderMapper.insertBuyNow(order);

        if (removeFromCart) {
            Integer cartId = toInteger(params.get("cartId"));
            if (cartId != null) {
                cartMapper.deleteById(cartId);
            }
        }
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer integer) {
            return integer;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(value.toString());
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        return new BigDecimal(value.toString());
    }
}
