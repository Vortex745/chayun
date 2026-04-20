package com.xu.tea.controller;

import com.xu.tea.common.Result;
import com.xu.tea.entity.Message;
import com.xu.tea.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/message")
@CrossOrigin
public class MessageController {

    @Autowired
    private MessageMapper messageMapper;

    // 1. 前台提交留言
    @PostMapping("/add")
    public Result<?> add(@RequestBody Message message) {
        if(message.getContent() == null || message.getContent().isEmpty()) {
            return Result.error("内容不能为空");
        }
        messageMapper.insert(message);
        return Result.success("留言发送成功");
    }

    // 2. 后台获取列表
    @GetMapping("/list")
    public Result<List<Message>> list() {
        // 按时间倒序
        return Result.success(messageMapper.selectAll());
    }

    @GetMapping("/user/{userId}")
    public Result<List<Message>> listByUserId(@PathVariable Integer userId) {
        return Result.success(messageMapper.selectByUserId(userId));
    }

    // 3. 后台获取未读数量 (用于角标)
    @GetMapping("/unread")
    public Result<Integer> getUnreadCount() {
        return Result.success(messageMapper.countUnread());
    }

    // 4. 标记为已读 (点击查看时调用)
    @PutMapping("/read/{id}")
    public Result<?> markRead(@PathVariable Integer id) {
        // 调用 Mapper 更新状态
        messageMapper.updateState(id, "已读");
        return Result.success("操作成功");
    }

    // 5. 删除留言 (修复：先删回复，再删留言)
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        // 先删除关联的回复记录
        messageMapper.deleteRepliesByMessageId(id);
        // 再删除留言本身
        messageMapper.deleteById(id);
        return Result.success("删除成功");
    }

    // 6. 管理员回复留言
    @PutMapping("/reply")
    public Result<?> reply(@RequestBody Message message) {
        // 这里的 message.getReply() 是前端传来的单次回复内容
        if (message.getId() == null || message.getReply() == null) {
            return Result.error("参数错误");
        }

        // 1. 插入到回复记录表
        messageMapper.insertReply(message.getId(), message.getReply());

        // 2. 更新主留言状态为 "已回复"
        messageMapper.updateState(message.getId(), "已回复");

        return Result.success("回复成功");
    }

}
