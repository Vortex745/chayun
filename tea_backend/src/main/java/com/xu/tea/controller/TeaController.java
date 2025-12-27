package com.xu.tea.controller;

import com.xu.tea.common.Result;
import com.xu.tea.entity.Tea;
import com.xu.tea.service.TeaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 标记这是个接口控制器，返回的是 JSON 数据
@RequestMapping("/tea") // 一级路径，所有这个类的接口都以 /tea 开头
@CrossOrigin
public class TeaController {

    @Autowired
    private TeaService teaService;

    // 最终访问地址：http://localhost:8081/tea/list
    @GetMapping("/list")
    public Result<List<Tea>> getTeaList() {
        // 1. 调用 Service 查询数据
        List<Tea> list = teaService.findAllTea();
        // 2. 包装成统一结果返回
        return Result.success(list);
    }
    // ... 原有的 getTeaList ...

    // 1. 接口：查询详情
    // URL: http://localhost:8080/tea/1
    @GetMapping("/{id}")
    public Result<Tea> getTeaDetail(@PathVariable Integer id) {
        Tea tea = teaService.findTeaById(id);
        return Result.success(tea);
    }

    // 2. 接口：新增茶叶 (必须用 POST)
    // URL: http://localhost:8080/tea/add
    @PostMapping("/add")
    public Result<?> addTea(@RequestBody Tea tea) {
        teaService.addTea(tea);
        return Result.success(tea); // 返回成功把新增的数据带回去
    }

    // 3. 接口：删除茶叶 (建议用 DELETE，用 GET 也行但不够规范)
    // URL: http://localhost:8080/tea/delete/1
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteTea(@PathVariable Integer id) {
        teaService.deleteTea(id);
        return Result.success("删除成功");
    }
    // ... 其他方法 ...

    // 4. 接口：更新茶叶 (通常用 PUT 请求)
    @PutMapping("/update")
    public Result<?> updateTea(@RequestBody Tea tea) {
        teaService.updateTea(tea);
        return Result.success("修改成功");
    }
}