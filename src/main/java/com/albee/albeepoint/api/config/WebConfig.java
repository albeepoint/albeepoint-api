package com.albee.albeepoint.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import com.albeepoint.api.config.interceptor.AccessControlInterceptor;

@Configuration
@RequiredArgsConstructor
@ComponentScan(basePackages = {"com.albeepoint.core", "com.albeepoint.api"}, excludeFilters = @ComponentScan.Filter(Configuration.class))
public class WebConfig extends WebMvcConfigurerAdapter {
    private final AccessControlInterceptor accessControlInterceptor;

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean(new ApiFilter());
        return registrationBean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(accessControlInterceptor)
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "*.ico", "/error", "/error-page/**");
                // .addPathPatterns("/url1", "/url2");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }

//    @Override
//   	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//   		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//   		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
//   		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
//   	}

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/org.webjars/swagger-ui/")
//                .addResourceLocations("classpath:/META-INF/resources/org.webjars/swagger-ui/4.18.2/")
                .resourceChain(false);
    }


}