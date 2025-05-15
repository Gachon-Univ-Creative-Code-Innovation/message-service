package com.gucci.message_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("http://127.0.0.1:5500", "http://localhost:5173","https://a-log.netlify.app","http://43.201.107.237:5173") // 테스트용 포트
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Cache-Control", "Content-Language", "Content-Type", "Expires", "Last-Modified", "Pragma")
                .allowCredentials(false)
                .maxAge(3600);

    }
}
