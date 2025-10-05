package com.kh.ypjp.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
        
        // 레시피 이미지 경로 
        registry.addResourceHandler("/community/recipe/**")
                .addResourceLocations(absolutePath);
        
        // 프로필 이미지 경로
        registry.addResourceHandler("/profile/**")
        		.addResourceLocations(absolutePath);
        
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://front.ypjp.store", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
