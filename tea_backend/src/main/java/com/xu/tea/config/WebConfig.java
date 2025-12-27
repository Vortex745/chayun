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
        // 只要访问 /images/** 的请求，都去硬盘的 D:/tea_images/ 下找
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath);
    }
}