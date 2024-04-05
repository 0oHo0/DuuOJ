 <p align=center><img src="https://cdn.jsdelivr.net/gh/0oHo0/Picture@main/img/202312181036449.jpg#pic_center" style="width: 40%;" /></p>
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

## 项目核心亮点 ⭐

1. 权限校验：用户权限校验，配合Redis实现不同设备单点登录
2. 代码沙箱（安全沙箱）
   - 用户代码藏毒：写个木马文件、修改系统权限
   - 沙箱：隔离的、安全的环境，用户的代码不会影响到沙箱之外的系统的运行
   - 资源分配：限制用户程序的占用资源
3. 判题规则
   - 题目用例的比对，结果的验证
4. 任务调度（消息队列执行判题）
   - 服务器资源有限，用户要排队，按照顺序去依次执行判题
   - 使用死信队列处理失败判题
5. 接口限流：使用Redis Zset结构配合滑动窗口思想削峰，并开发接口限流注解
6. 缓存题目信息、评论与点赞，使用定时任务进行持久化与缓存预热
7. 网关进行统一用户鉴权

##  项目功能

### 网关

1. 用户鉴权
2. 请求转发

### 题目模块

1. 题目与提交列表增删改查
2. 统计题目通过率
3. 在线做题，提交代码（用户/管理）
4. 对题目进行评论点赞

### 用户模块

1. 注册登录
2. 用户管理（管理员）
3. 用户上传头像功能，使用阿里云对象存储OSS存储图片（不一定）

### 判题模块

提交判题：检查运行结果

### 代码沙箱

- 只负责接受代码和输入，运行代码，返回编译运行的结果，不用管用户提交的程序是否正确(不负责判题)

##   项目业务流程

### 核心流程时序

![image-20240329171656990](https://cdn.jsdelivr.net/gh/0oHo0/Picture@main/img/202403291716119.png)

### 架构设计

![OJConstruct](https://cdn.jsdelivr.net/gh/0oHo0/Picture@main/img/202403292014021.png)

## 项目结构

- Duuoj-backend  后端单体项目

- Duuoj-backend-microservice  后端微服务项目
  - Duuoj-backend-common 公共类
  - Duuoj-backend-gateway 项目网关  端口：8104
  - Duuoj-backend-judge-service 判题服务   端口：8105
  - Duuoj-backend-model  单体类  
  - Duuoj-backend-question-service 题目服务   端口：8106
  - Duuoj-backend-service-client  OpenFeign服务调用客户端
  - Duuoj-backend-user-service 用户服务   端口：8108
  
- Duuoj-frontend  前端项目

- Duuoj-Sandbox 代码沙箱：编译及运行代码

## 待扩展点  

