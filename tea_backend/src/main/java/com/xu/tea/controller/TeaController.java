package com.xu.tea.controller;

import com.xu.tea.common.Result;
import com.xu.tea.entity.Tea;
import com.xu.tea.mapper.TeaMapper; // 必须导入这个
import com.xu.tea.service.TeaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tea")
@CrossOrigin
public class TeaController {

    // 【关键修复】你之前漏掉了这几行！
    // 必须先把 teaMapper 注入进来，下面才能用
    @Autowired
    private TeaMapper teaMapper;

    @Autowired
    private TeaService teaService;

    // 1. 查询所有
    @GetMapping("/list")
    public Result<List<Tea>> list() {
        return Result.success(teaMapper.selectAll());
    }

    // 2. 新增
    @PostMapping("/add")
    public Result<?> add(@RequestBody Tea tea) {
        teaService.addTea(tea);
        return Result.success("新增成功");
    }

    // 3. 删除
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        teaMapper.deleteById(id);
        return Result.success("删除成功");
    }

    // 4. 更新
    @PutMapping("/update")
    public Result<?> updateTea(@RequestBody Tea tea) {
        teaService.updateTea(tea);
        return Result.success("修改成功");
    }

    // 5. 【搜索接口】
    @GetMapping("/search")
    public Result<List<Tea>> search(@RequestParam String name) {
        // 这里使用小写的 teaMapper 对象，而不是大写的 TeaMapper 类
        return Result.success(teaMapper.selectByName(name));
    }
    @GetMapping("/{id}")
    public Result<Tea> getById(@PathVariable Integer id) {
        return Result.success(teaMapper.selectById(id));
    }
    @GetMapping("/type")
    public Result<List<Tea>> listByType(@RequestParam String type) {
        return Result.success(teaMapper.selectByType(type));
    }
}
