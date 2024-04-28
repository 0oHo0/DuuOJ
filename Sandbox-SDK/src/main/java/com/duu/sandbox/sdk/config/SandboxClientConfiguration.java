package com.duu.sandbox.sdk.config;

import com.duu.sandbox.sdk.SandboxClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Duu
 * @description
 * @date 2024/04/27 15:49
 * @from https://github.com/0oHo0
 **/
@ConfigurationProperties(prefix = "duuoj.sandbox")
@Configuration
@ComponentScan
@Data
public class SandboxClientConfiguration {

    private String accessKey;

    private String secretKey;
    @Bean
    public SandboxClient sandboxClient() {
        return new SandboxClient(accessKey,secretKey);
    }
}