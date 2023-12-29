package com.duu.ojquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.duu.ojquestionservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.duu")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.duu.ojserviceclient.service"})
public class DuuojBackendQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DuuojBackendQuestionServiceApplication.class, args);
    }

}
