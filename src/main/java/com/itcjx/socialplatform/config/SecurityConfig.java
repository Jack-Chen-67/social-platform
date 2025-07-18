package com.itcjx.socialplatform.config;

import com.itcjx.socialplatform.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                // 禁用CSRF（新版本已标记为即将移除）
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 新版本的权限配置方式
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("article/getArticle/**").permitAll()
                        .requestMatchers("likes/{articleId}/**").permitAll()
                        .requestMatchers("commentLike/getCommentLikeCount/{commentId}**").permitAll()
                        .anyRequest()
                        //.permitAll()
                        .authenticated()
                )

                // 解决X-Content-Type问题
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                );

        return http.build();
    }
}
