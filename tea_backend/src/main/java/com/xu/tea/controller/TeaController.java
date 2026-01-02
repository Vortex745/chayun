package com.xu.tea.controller;

import com.xu.tea.common.Result;
import com.xu.tea.entity.Tea;
import com.xu.tea.mapper.TeaMapper;
import com.xu.tea.service.TeaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tea")
@CrossOrigin
public class TeaController {

    @Autowired
    private TeaMapper teaMapper;

    @Autowired
    private TeaService teaService;

    // 1. 查询所有
    @GetMapping("/list")
    public Result<List<Tea>> list() {
        // 这里可以按 create_time 倒序，或者按 click_count 倒序（热门商品在前）
        return Result.success(teaMapper.selectAll());
    }

    // 2. 新增
    @PostMapping("/add")
    public Result<?> add(@RequestBody Tea tea) {
        // 新增时初始化浏览量为0
        tea.setClickCount(0);
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

    // 5. 搜索接口
    @GetMapping("/search")
    public Result<List<Tea>> search(@RequestParam String name) {
        return Result.success(teaMapper.selectByName(name));
    }

    // 6. 【重点修改】获取详情 + 浏览量增加
    @GetMapping("/{id}")
    public Result<Tea> getById(@PathVariable Integer id) {
        Tea tea = teaMapper.selectById(id);
        if (tea != null) {
            // 简单的浏览量 +1 逻辑
            int currentCount = (tea.getClickCount() == null) ? 0 : tea.getClickCount();
            tea.setClickCount(currentCount + 1);

            // 更新回数据库
            // 注意：这里复用了 updateTea 逻辑，这在毕设中是完全可以的
            // 如果要更严谨，应该写 SQL: update t_tea set click_count = click_count + 1 where id = ?
            teaService.updateTea(tea);
        }
        return Result.success(tea);
    }

    @GetMapping("/type")
    public Result<List<Tea>> listByType(@RequestParam String type) {
        return Result.success(teaMapper.selectByType(type));
    }
}