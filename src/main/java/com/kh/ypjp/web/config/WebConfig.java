package com.kh.ypjp.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private String getProjectRoot() {
        return System.getProperty("user.dir");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String webPath = "resources/";
        String projectRoot = getProjectRoot();
        String absolutePath = "file:///" + projectRoot + "/" + webPath;

        registry.addResourceHandler("/images/**")
                .addResourceLocations(absolutePath);
    }
}