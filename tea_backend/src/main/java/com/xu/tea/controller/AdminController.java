package com.xu.tea.controller;

import com.xu.tea.common.Result;
import com.xu.tea.entity.Admin;
import com.xu.tea.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminMapper adminMapper;

    // 登录接口
    @PostMapping("/login")
    public Result<?> login(@RequestBody Admin admin) {
        // 1. 根据用户名去数据库查
        Admin dbAdmin = adminMapper.selectByUsername(admin.getUsername());

        // 2. 判断用户是否存在
        if (dbAdmin == null) {
            return Result.error("账号不存在");
        }

        // 3. 判断密码是否正确 (注意：实际项目这里要加密，毕设明文即可)
        if (!dbAdmin.getPassword().equals(admin.getPassword())) {
            return Result.error("密码错误");
        }

        // 4. 登录成功，把用户信息返回给前端
        dbAdmin.setPassword(null); // 安全起见，不把密码传回去
        return Result.success(dbAdmin);
    }
}