package com.xu.tea.mapper;

import com.xu.tea.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 对应 id="selectAll"
    List<Message> selectAll();

    List<Message> selectByUserId(Integer userId);

    // 对应 id="countUnread"
    Integer countUnread();

    // 对应 id="insert"
    int insert(Message message);

    // 对应 id="updateState"
    int updateState(@Param("id") Integer id, @Param("state") String state);

    // 对应 id="deleteById" (只删除留言本身)
    int deleteById(Integer id);

    // 新增方法
    int updateReply(@Param("id") Integer id, @Param("reply") String reply, @Param("state") String state);
    int insertReply(@Param("messageId") Integer messageId, @Param("content") String content);

    // 【新增】根据留言ID删除关联的回复
    int deleteRepliesByMessageId(Integer messageId);
}
