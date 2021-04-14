package com.cau.cc;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/api/v2/api-docs","redirect:/v2/api-docs");
        registry.addRedirectViewController("/api/configuration/ui","redirect:/configuration/ui");
        registry.addRedirectViewController("/api/configuration/security","redirect:/configuration/security");
        registry.addRedirectViewController("/api/swagger-resources","redirect:/swagger-resources");
        registry.addRedirectViewController("/api/swagger","redirect:/swagger-ui.html");
        registry.addRedirectViewController("/api/swagger/","redirect:/swagger-ui.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/**").addResourceLocations("classpath:/META-INF/resources/");
    }
}