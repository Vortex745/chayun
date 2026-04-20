package com.xu.tea.controller;

import com.xu.tea.common.Result;
import com.xu.tea.entity.Carousel;
import com.xu.tea.mapper.CarouselMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carousel")
@CrossOrigin
public class CarouselController {

    @Autowired
    private CarouselMapper carouselMapper;

    // --- 后台管理接口 ---

    @GetMapping("/list")
    public Result<List<Carousel>> list() {
        return Result.success(carouselMapper.selectAll());
    }

    @PostMapping("/save")
    public Result<?> save(@RequestBody Carousel carousel) {
        if (carousel.getId() == null) {
            carouselMapper.insert(carousel);
        } else {
            carouselMapper.update(carousel);
        }
        return Result.success("保存成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        carouselMapper.deleteById(id);
        return Result.success("删除成功");
    }

    // --- 前台用户接口 ---

    @GetMapping("/active")
    public Result<List<Carousel>> getActive() {
        return Result.success(carouselMapper.selectActive());
    }
}