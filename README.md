<p align="center">
<a>
    <img src="https://img.shields.io/badge/Spring Boot-2.7.2-brightgreen.svg" alt="Spring Boot">
    <img src="https://img.shields.io/badge/MySQL-8.0.20-orange.svg" alt="MySQL">
    <img src="https://img.shields.io/badge/Java-1.8.0__371-blue.svg" alt="Java">
    <img src="https://img.shields.io/badge/Redis-5.0.14-red.svg" alt="Redis">
    <img src="https://img.shields.io/badge/RabbitMQ-3.9.11-orange.svg" alt="RabbitMQ">
    <img src="https://img.shields.io/badge/MyBatis--Plus-3.5.2-blue.svg" alt="MyBatis-Plus">
    <img src="https://img.shields.io/badge/Redisson-3.21.3-yellow.svg" alt="Redisson">
    <img src="https://img.shields.io/badge/Gson-3.9.1-blue.svg" alt="Gson">
    <img src="https://img.shields.io/badge/Hutool-5.8.8-green.svg" alt="Hutool">
    <img src="https://img.shields.io/badge/MyBatis-2.2.2-yellow.svg" alt="MyBatis">
</a>
</p>

#    DuuOJ判题平台

##  项目介绍

基于 Spring Boot + Spring Cloud Alibaba 微服务 + Docker + RabbitMQ + Vue 3 的 **编程算法题目在线评测系统** 

> 项目来源  编程导航（https://yupi.icu）

##  项目功能

###  题目模块

1. 对题目的增删改查

##   项目业务流程



## 项目结构

- Duuoj-backend  后端单体项目
- Duuoj-backend-microservice  后端微服务项目
  - Duuoj-backend-common 公共类
  - Duuoj-backend-gateway 项目网关
  - Duuoj-backend-judge-service 判题服务
  - Duuoj-backend-model  单体类
  - Duuoj-backend-question-service 题目服务
  - Duuoj-backend-service-client  OpenFeign服务调用客户端
  - Duuoj-backend-user-service 用户服务
- Duuoj-frontend  前端项目
- Duuoj-Sandbox 代码沙箱：编译及运行代码