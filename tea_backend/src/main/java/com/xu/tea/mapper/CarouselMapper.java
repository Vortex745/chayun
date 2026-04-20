package com.xu.tea.mapper;

import com.xu.tea.entity.Carousel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CarouselMapper {
    // 1. 管理员查询所有
    @Select("SELECT * FROM t_carousel ORDER BY sort ASC, create_time DESC")
    List<Carousel> selectAll();

    // 2. 用户端查询所有可见的 (is_show = 1)
    @Select("SELECT * FROM t_carousel WHERE is_show = 1 ORDER BY sort ASC")
    List<Carousel> selectActive();

    // 3. 新增
    @Insert("INSERT INTO t_carousel(img_url, link_text, sort, is_show, create_time) " +
            "VALUES(#{imgUrl}, #{linkText}, #{sort}, #{isShow}, NOW())")
    void insert(Carousel carousel);

    // 4. 修改
    @Update("UPDATE t_carousel SET img_url=#{imgUrl}, link_text=#{linkText}, " +
            "sort=#{sort}, is_show=#{isShow} WHERE id=#{id}")
    void update(Carousel carousel);

    // 5. 删除
    @Delete("DELETE FROM t_carousel WHERE id=#{id}")
    void deleteById(Integer id);
}