package com.itcjx.socialplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties
@MapperScan("com.itcjx.socialplatform.mapper")
public class SocialPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(SocialPlatformApplication.class, args);
    }

    @Bean
    public CommandLineRunner testAI(ChatClient chatClient) {
        return args -> {
            // 测试同义词生成
            String prompt = "生成与'电脑'相关的3个同义词，用逗号分隔";
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            System.out.println("AI测试结果：" + response);
        };
    }
}
