package com.work.erpsystem.config;

import com.work.erpsystem.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private final UserServiceImpl userService;

    @Autowired
    public MvcConfig(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/error").setViewName("error");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new OrgHttpInterceptor(userService)).excludePathPatterns(
                "/register", "/login", "/create_org", "/error", "/v3/**", "/api/auth/**",
                "/set_new_user/**", "/**/api/**"
        );
    }
}
