package com.duu.ojuserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.duu.ojuserservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.duu")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.duu.ojserviceclient.service"})
public class DuuojBackendUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DuuojBackendUserServiceApplication.class, args);
    }

}
