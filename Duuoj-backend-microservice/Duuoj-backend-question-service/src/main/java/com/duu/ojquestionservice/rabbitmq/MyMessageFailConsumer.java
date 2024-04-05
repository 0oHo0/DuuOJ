package com.duu.ojquestionservice.rabbitmq;

import com.duu.ojcommon.common.ErrorCode;
import com.duu.ojcommon.exception.BusinessException;
import com.duu.ojmodel.model.entity.QuestionSubmit;
import com.duu.ojmodel.model.enums.QuestionSubmitStatusEnum;
import com.duu.ojquestionservice.service.QuestionSubmitService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author : duu
 * @data : 2024/3/10
 * @from ：https://github.com/0oHo0
 **/
@Component
@Slf4j
public class MyMessageFailConsumer {

    @Resource
    private QuestionSubmitService questionSubmitService;
    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {"code_dlx_queue"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        long questionSubmitId = Long.parseLong(message);
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交的题目信息不存在");
        }
        // 把提交题目标为失败
        questionSubmit.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
        boolean update = questionSubmitService.updateById(questionSubmit);
        if (!update) {
            log.info("处理死信队列消息失败,对应提交的题目id为:{}", questionSubmit.getId());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "处理死信队列消息失败");
        }
        // 确认消息
        channel.basicAck(deliveryTag, false);
    }
}
