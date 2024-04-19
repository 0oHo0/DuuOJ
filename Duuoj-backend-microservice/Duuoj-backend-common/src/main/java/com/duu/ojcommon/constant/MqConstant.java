package com.duu.ojcommon.constant;

/**
 * @author : duu
 * @date : 2024/3/13
 * @from ：https://github.com/0oHo0
 * 消息队列
 **/
public interface MqConstant {
    //普通队列参数
    String CODE_QUEUE = "code_queue";
    String CODE_EXCHANGE = "code_exchange";
    String CODE_ROUTING_KEY = "code_routingKey";
    String DIRECT_EXCHANGE = "direct";
    //死信队列名
    String CODE_DLX_QUEUE = "code_dlx_queue";
    //死信队列交换机
    String CODE_DLX_EXCHANGE = "code_dlx_exchange";
    //死信队列路由键
    String CODE_DLX_ROUTING_KEY = "code_dlx_routingKey";
}
