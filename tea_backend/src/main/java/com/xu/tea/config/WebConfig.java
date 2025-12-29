package com.xu.tea.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${tea.images.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 关键点：添加 file: 前缀
        // 如果 uploadPath 是 "D:/tea_images/"，这里拼起来就是 "file:D:/tea_images/"
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath);
    }
}