package com.elemental.tech.workitems.config;

import com.elemental.tech.workitems.interceptor.IpAuditingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class IpAuditingConfig implements WebMvcConfigurer {

    private final IpAuditingInterceptor ipAuditingInterceptor;

    public IpAuditingConfig(IpAuditingInterceptor ipAuditingInterceptor) {
        this.ipAuditingInterceptor = ipAuditingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ipAuditingInterceptor);
    }
}