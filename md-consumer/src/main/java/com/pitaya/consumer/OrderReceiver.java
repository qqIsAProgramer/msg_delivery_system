package com.pitaya.consumer;

import com.pitaya.model.Order;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @Author: qyl
 * @Date: 2021/4/15 19:05
 */
@Component
@Slf4j
public class OrderReceiver {

    // 配置监听的哪一个队列，同时在没有 queue 和 exchange 的情况下会去创建并建立绑定关系
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order-queue", durable = "true"),
            exchange = @Exchange(value = "order-exchange", type = "topic"),
            key = "order.#"
        )
    )
    @RabbitHandler  // 表明此方法为处理消息的方法
    public void receiveOrderMessage(@Payload Order order,
                                    @Headers Map<String, Object> headers,
                                    Channel channel) throws IOException {
        log.info("处理收到的消息...");
        log.info("订单ID: [{}]", order.getId());
        /*
         * Delivery Tag 用来标识信道中投递的消息
         * RabbitMQ 推送消息给 Consumer 时，会附带一个 Delivery Tag，以便 Consumer 可以在消息确认时告诉 RabbitMQ 到底是哪条消息被确认了
         * RabbitMQ 保证在每个信道中，每条消息的 Delivery Tag 从 1 开始递增
         */
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        /*
         *  multiple 取值为 false 时，表示通知 RabbitMQ 当前消息被确认
         *  如果为 true，则额外将比第一个参数指定的 delivery tag 小的消息一并确认
         */
        boolean multiple = false;

        // 此处处理业务...

        // 确认一条消息以被消费
        channel.basicAck(deliveryTag, multiple);
    }
}
