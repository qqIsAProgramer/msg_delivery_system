package com.pitaya.producer;

import com.pitaya.mapper.BrokerMessageLogMapper;
import com.pitaya.model.Order;
import com.pitaya.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author: qyl
 * @Date: 2021/4/15 22:27
 */
@Component
@Slf4j
public class OrderSender {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private BrokerMessageLogMapper brokerMessageLogMapper;

    /**
     * 回调函数：confirm 确认
     */
    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            log.info("correlationData: [{}]", correlationData);
            String messageId = correlationData.getId();
            if (ack) {
                // 如果 confirm 返回成功，则进行更新
                brokerMessageLogMapper.updateLogStatus(messageId, Constants.ORDER_SEND_SUCCESS, new Date());
            } else {
                // 消息回传失败则进行具体的后续操作：重试或者补偿等手段
                log.info("delivery failed...");
            }
        }
    };

    /**
     * 发送消息方法调用: 构建自定义对象消息
     */
    public void sendMessage(Order order) {
        // 通过实现 ConfirmCallback 接口，消息发送到 Broker 后触发回调，确认消息是否到达 Broker 服务器，也就是只确认是否正确到达 Exchange 中
        rabbitTemplate.setConfirmCallback(confirmCallback);
        // 消息唯一ID
        CorrelationData correlationData = new CorrelationData(order.getMessageId());
        rabbitTemplate.convertAndSend("order-exchange", "order.msg", order, correlationData);
    }
}
