package com.pitaya.task;

import com.alibaba.fastjson.JSON;
import com.pitaya.mapper.BrokerMessageLogMapper;
import com.pitaya.model.BrokerMessageLog;
import com.pitaya.model.Order;
import com.pitaya.producer.OrderSender;
import com.pitaya.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author: qyl
 * @Date: 2021/4/16 9:18
 */
@Component
@Slf4j
public class RetryTask {

    @Resource
    private OrderSender orderSender;

    @Resource
    private BrokerMessageLogMapper brokerMessageLogMapper;

    /**
     * 定时扫描未成功投递的消息
     */
    @Scheduled(initialDelay = 5 * 1000, fixedDelay = 5 * 1000)
    public void resendMessage() {
        log.info("定时任务进行中...");
        // 查询消息状态为 0(发送中)，且已经超时的消息集合
        List<BrokerMessageLog> logs = brokerMessageLogMapper.querySendingAndTimeoutMessage();
        logs.forEach(messageLog -> {
            if(messageLog.getTryCount() >= 3){
                // update failure message
                brokerMessageLogMapper.updateLogStatus(messageLog.getMessageId(), Constants.ORDER_SEND_FAILURE, new Date());
            } else {
                // resend
                brokerMessageLogMapper.updateRetry(messageLog.getMessageId(), new Date());
                Order resendOrder = JSON.parseObject(messageLog.getMessage(), Order.class);
                try {
                    orderSender.sendMessage(resendOrder);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Send message failed...");
                }
            }
        });
    }
}
