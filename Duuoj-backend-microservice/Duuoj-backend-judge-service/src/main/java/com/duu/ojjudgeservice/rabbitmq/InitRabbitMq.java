package com.duu.ojjudgeservice.rabbitmq;


import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 */
@Slf4j
public class InitRabbitMq {

    public static void main(String[] args) {
        InitRabbitMqBean initRabbitMqBean = new InitRabbitMqBean();
        initRabbitMqBean.init();
    }
}
