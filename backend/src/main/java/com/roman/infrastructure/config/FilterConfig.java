package com.roman.infrastructure.config;

import com.roman.infrastructure.logging.RequestResponseLoggingFilter;
import com.roman.infrastructure.security.RateLimitingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilterRegistration(RateLimitingFilter filter) {
        FilterRegistrationBean<RateLimitingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/api/v1/auth/*");
        registration.setOrder(1);
        return registration;
    }

    /**
     * Precisa rodar antes de tudo (inclusive do rate limiting e da SecurityFilterChain)
     * pra logar todo request/response, mesmo os bloqueados por rate limit (429) ou
     * rejeitados por autenticação/autorização (401/403).
     */
    @Bean
    public FilterRegistrationBean<RequestResponseLoggingFilter> requestResponseLoggingFilterRegistration(
            RequestResponseLoggingFilter filter) {
        FilterRegistrationBean<RequestResponseLoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
