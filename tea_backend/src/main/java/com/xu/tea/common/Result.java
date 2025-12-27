package com.xu.tea.common;

import lombok.Data;

/**
 * 统一返回结果类
 * 前端收到的 JSON 格式就是这个样子的
 */
@Data
public class Result<T> {
    private String code; // 状态码，比如 "200" 代表成功
    private String msg;  // 提示信息，比如 "查询成功"
    private T data;      // 返回的数据，泛型 T 代表可以是任何类型

    // 快速生成成功结果的方法
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode("200");
        result.setMsg("成功");
        result.setData(data);
        return result;
    }

    // 快速生成失败结果的方法
    public static Result<?> error(String msg) {
        Result<?> result = new Result<>();
        result.setCode("500");
        result.setMsg(msg);
        return result;
    }
}