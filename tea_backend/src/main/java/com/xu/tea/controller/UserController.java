package com.xu.tea.controller;

import com.xu.tea.common.Result;
import com.xu.tea.entity.User;
import com.xu.tea.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserMapper userMapper;

    // --- 管理员接口 ---
    @GetMapping("/list")
    public Result<List<User>> list() {
        return Result.success(userMapper.selectAll());
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        userMapper.deleteById(id);
        return Result.success("删除成功");
    }

    // --- 新增：用户端接口 ---

    // 1. 注册接口
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        // 检查用户名是否重复
        User exist = userMapper.selectByUsername(user.getUsername());
        if (exist != null) {
            return Result.error("用户名已存在");
        }
        // 插入数据库
        // 默认昵称设为用户名，方便显示
        if(user.getNickname() == null) user.setNickname(user.getUsername());
        userMapper.insert(user);
        return Result.success("注册成功");
    }

    // 2. 登录接口
    @PostMapping("/login")
    public Result<?> login(@RequestBody User user) {
        User dbUser = userMapper.selectByUsername(user.getUsername());
        if (dbUser == null) {
            return Result.error("账号不存在");
        }
        if (!dbUser.getPassword().equals(user.getPassword())) {
            return Result.error("密码错误");
        }
        dbUser.setPassword(null); // 隐藏密码
        return Result.success(dbUser);
    }
    @PutMapping("/update")
    public Result<?> update(@RequestBody User user) {
        userMapper.update(user);
        return Result.success("修改成功");
    }
}