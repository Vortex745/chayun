package com.xu.tea.controller;

import com.xu.tea.common.Result;
import com.xu.tea.entity.Cart;
import com.xu.tea.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartMapper cartMapper;

    // 1. 获取我的购物车列表
    // 前端会传 userId 过来 (例如 /cart/list?userId=1)
    @GetMapping("/list")
    public Result<List<Cart>> list(@RequestParam Integer userId) {
        return Result.success(cartMapper.selectByUserId(userId));
    }

    // 2. 加入购物车 (核心逻辑)
    @PostMapping("/add")
    public Result<?> add(@RequestBody Cart cart) {
        // 先查一下，这个用户有没有加购过这个商品
        Cart existCart = cartMapper.selectByUserIdAndTeaId(cart.getUserId(), cart.getTeaId());

        if (existCart != null) {
            // 情况A: 已经有了 -> 数量累加
            int newCount = existCart.getCount() + cart.getCount();
            cartMapper.updateCount(existCart.getId(), newCount);
        } else {
            // 情况B: 还没有 -> 新增一条
            cartMapper.insert(cart);
        }
        return Result.success("加入成功");
    }

    // 3. 更新数量 (在购物车页面点击 + - 号时调用)
    @PostMapping("/update/{id}/{count}")
    public Result<?> update(@PathVariable Integer id, @PathVariable Integer count) {
        cartMapper.updateCount(id, count);
        return Result.success("更新成功");
    }

    // 4. 删除
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        cartMapper.deleteById(id);
        return Result.success("删除成功");
    }
}