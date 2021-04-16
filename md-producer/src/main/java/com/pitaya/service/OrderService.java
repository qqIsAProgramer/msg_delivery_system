package com.pitaya.service;

import com.alibaba.fastjson.JSON;
import com.pitaya.mapper.BrokerMessageLogMapper;
import com.pitaya.mapper.OrderMapper;
import com.pitaya.model.BrokerMessageLog;
import com.pitaya.model.Order;
import com.pitaya.producer.OrderSender;
import com.pitaya.utils.Constants;
import com.pitaya.utils.DateUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author: qyl
 * @Date: 2021/4/15 22:27
 */
@Service
public class OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private IdWorker idWorker;

    @Resource
    private OrderSender orderSender;

    @Resource
    private BrokerMessageLogMapper brokerMessageLogMapper;

    /**
     * 创建订单
     */
    public void createOrder(Order order) {
        // 雪花算法生成ID
        order.setMessageId(String.valueOf(idWorker.nextId()));
        orderMapper.insertOrder(order);

        Date orderTime = new Date();
        // 存储消息记录
        BrokerMessageLog log = new BrokerMessageLog();
        log.setMessageId(order.getMessageId());
        log.setMessage(JSON.toJSONString(order));
        // 消息状态设置为 0，表示发送中
        log.setStatus(Constants.ORDER_SENDING);
        log.setCreateTime(orderTime);
        log.setUpdateTime(orderTime);
        // 设置消息未确认超时时间窗口为一分钟
        log.setNextRetry(DateUtil.setTimeout(orderTime, Constants.ORDER_TIMEOUT));
        // 插入数据库
        brokerMessageLogMapper.insertLog(log);

        // 发送消息
        orderSender.sendMessage(order);
    }
}
