package com.xu.tea.service;

import com.xu.tea.entity.Tea;
import com.xu.tea.mapper.TeaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // 标记这是一个业务类，Spring 会把它加载到容器里
public class TeaService {

    private final TeaMapper teaMapper;

    public TeaService(TeaMapper teaMapper) {
        this.teaMapper = teaMapper;
    }

    public List<Tea> findAllTea() {
        // 调用 Mapper 层的方法
        return teaMapper.selectAll();
    }
    // ... 原有的 findAllTea ...

    // 1. 业务：查询详情
    public Tea findTeaById(Integer id) {
        return teaMapper.selectById(id);
    }

    // 2. 业务：新增茶叶
    public void addTea(Tea tea) {
        // 这里以后可以加逻辑，比如：判断价格不能为负数
        teaMapper.insert(tea);
    }

    // 3. 业务：删除茶叶
    public void deleteTea(Integer id) {
        teaMapper.deleteById(id);
    }
    // ... 其他方法 ...

    // 4. 业务：更新茶叶
    public void updateTea(Tea tea) {
        teaMapper.update(tea);
    }
}