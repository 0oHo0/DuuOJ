 <p align=center><img src="https://cdn.jsdelivr.net/gh/0oHo0/Picture@main/img/202312181036449.jpg#pic_center" style="width: 40%;" /></p>
<p align="center">
<a>
    <img src="https://img.shields.io/badge/Spring Boot-2.7.2-brightgreen.svg" alt="Spring Boot">
    <img src="https://img.shields.io/badge/MySQL-8.0.20-orange.svg" alt="MySQL">
    <img src="https://img.shields.io/badge/Java-1.8-blue.svg" alt="Java">
    <img src="https://img.shields.io/badge/Redis-5.0.14-red.svg" alt="Redis">
    <img src="https://img.shields.io/badge/RabbitMQ-3.9.11-orange.svg" alt="RabbitMQ">
    <img src="https://img.shields.io/badge/MyBatis--Plus-3.5.2-blue.svg" alt="MyBatis-Plus">
    <img src="https://img.shields.io/badge/Redisson-3.21.3-yellow.svg" alt="Redisson">
    <img src="https://img.shields.io/badge/Gson-3.9.1-blue.svg" alt="Gson">
    <img src="https://img.shields.io/badge/Hutool-5.8.8-green.svg" alt="Hutool">
    <img src="https://img.shields.io/badge/MyBatis-2.2.2-yellow.svg" alt="MyBatis">
     <img src="https://img.shields.io/badge/Elasticsearch-7.17.9-blue.svg" alt="Elasticsearch">
     <img src="https://img.shields.io/badge/DuuRPC-0.0.1-green.svg" alt="DuuRPC">
</a>
</p>




#    DuuOJ判题平台

##  项目介绍

基于 Spring Boot + Spring Cloud Alibaba 微服务 + Redis + MySQL + Docker + RabbitMQ + Elasticsearch + Vue 3 的 **编程算法题目在线评测系统** 

## 项目核心亮点 ⭐

1. 权限校验：配合网关JWT用户权限校验，使用Redis实现不同设备单点登录

2. 代码沙箱（Docker与Java `Runtime`两种方式实现）

   - 使用 JavaSecurity 与字典树限制代码权限

   - 用户代码藏毒：写个木马文件、修改系统权限
   - 沙箱：隔离的、安全的环境，用户的代码不会影响到沙箱之外的系统的运行
   - 资源分配：限制用户程序的占用资源

3. 任务调度（消息队列执行判题）
   - 服务器资源有限，用户要排队，按照顺序去依次执行判题
   - 使用死信队列处理失败判题

4. 接口限流：使用Redisson的RRateLimiter，根据用户ip限流，开发注解简化调用

5. 缓存题目信息，使用定时任务进行持久化与缓存预热，解决缓存穿透、雪崩与击穿

6. Redis bitmap结构实现站内日活跃度统计，减少内存占用

7. 使用 Elasticsearch 配合 `ik_max_word`(存储)和`ik_max`(查询)两种分词器对题目搜索进行优化

8. 开发代码沙箱的Spring Boot Start，并进行签名认证，方便其他开发者调用

##  项目功能

### 网关

1. 用户鉴权
2. 请求转发

### 题目模块

1. 题目与提交列表增删改查
2. 统计题目通过率
3. 在线做题，提交代码
4. 对题目进行评论、点赞
5. Elasticsearch优化题目搜索，提高搜索命中率

### 用户模块

1. 注册登录
2. 用户管理
3. 个人信息管理

### 判题模块

- 向代码沙箱提交代码
- 对沙箱返回结果使用策略模式执行判题

### 代码沙箱

- 支持Java、CPP两种语言运行环境

- 代码执行支持本地ACM模式与Docker 参数输入模式
- 使用字典树检查代码安全性
- 统计代码用时
- 使用SpringBoot Start调用时进行签名认证

> 只负责接受代码和输入，运行代码，返回编译运行的结果，不用管用户提交的程序是否正确(不负责判题)

### 任务

- 初始化Redis的Bloom过滤器
- 同步题目信息至Elasticsearch、Redis（缓存预热）
- `@Scheduled`定时任务在空闲时同步题目信息，兜底保证缓存一致性

##   项目业务流程

### 核心流程时序

![image-20240329171656990](https://cdn.jsdelivr.net/gh/0oHo0/Picture@main/img/202403291716119.png)

### 架构设计

![OJConstruct](https://cdn.jsdelivr.net/gh/0oHo0/Picture@main/img/202403292014021.png)

## 项目结构

- Duuoj-backend  后端单体项目

- Duuoj-backend-microservice  后端微服务项目
  - Duuoj-backend-common 公共类
  - Duuoj-backend-gateway 项目网关
  - Duuoj-backend-judge-service 判题服务
  - Duuoj-backend-model  实体类  
  - Duuoj-backend-question-service 题目服务
  - Duuoj-backend-service-client  OpenFeign远程调用客户端
  - Duuoj-backend-user-service 用户服务
  
- Duuoj-frontend  前端项目

- Duuoj-Sandbox 代码沙箱：编译及运行代码

## 待扩展点  

