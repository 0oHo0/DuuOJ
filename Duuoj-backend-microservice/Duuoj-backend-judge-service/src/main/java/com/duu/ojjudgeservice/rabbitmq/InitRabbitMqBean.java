package com.duu.ojjudgeservice.rabbitmq;

import com.duu.ojcommon.constant.MqConstant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import static com.duu.ojcommon.constant.MqConstant.*;
/**
 * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 */
@Slf4j
@Component
public class InitRabbitMqBean {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @PostConstruct
    public void init() {
        try {

            ConnectionFactory factory = new ConnectionFactory();
            //factory.setHost(host);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            //删除
            channel.queueDelete(CODE_QUEUE);
            channel.queueDelete(CODE_DLX_QUEUE);
            channel.exchangeDelete(CODE_EXCHANGE);
            channel.exchangeDelete(CODE_DLX_EXCHANGE);

            channel.exchangeDeclare(CODE_EXCHANGE, DIRECT_EXCHANGE);
            //死信队列创建
            channel.exchangeDeclare(CODE_DLX_EXCHANGE,DIRECT_EXCHANGE);
            channel.queueDeclare(CODE_DLX_QUEUE,true,false,false,null);
            channel.queueBind(CODE_DLX_QUEUE,CODE_DLX_EXCHANGE,CODE_DLX_ROUTING_KEY);

            // 创建消息队列 绑定死信队列
            Map<String, Object> args = new HashMap<>();
            args.put("x-dead-letter-exchange",CODE_DLX_EXCHANGE);
            args.put("x-dead-letter-routing-key",CODE_DLX_ROUTING_KEY);
            channel.queueDeclare(CODE_QUEUE, true, false, false, args);
            channel.queueBind(CODE_QUEUE, CODE_EXCHANGE, CODE_ROUTING_KEY);
            log.info("消息队列启动成功");
        } catch (Exception e) {
            log.error("消息队列启动失败", e);
        }
    }
}
