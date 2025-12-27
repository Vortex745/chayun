package com.xu.tea.controller;

import com.xu.tea.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@CrossOrigin // 允许跨域
public class CommonController {

    // 读取配置文件里的路径
    @Value("${tea.images.path}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择图片");
        }

        // 1. 获取原文件名后缀 (如 .jpg)
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 2. 生成新文件名 (UUID防止重名)
        String newFileName = UUID.randomUUID().toString() + suffix;

        // 3. 创建目标文件
        File dest = new File(uploadPath + newFileName);

        // 确保目录存在
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        try {
            // 4. 保存文件到硬盘
            file.transferTo(dest);

            // 5. 返回可访问的URL (映射路径 /images/)
            String url = "http://localhost:8080/images/" + newFileName;
            return Result.success(url);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}