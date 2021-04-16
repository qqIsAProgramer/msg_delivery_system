package com.pitaya.controller;

import com.pitaya.model.Order;
import com.pitaya.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: qyl
 * @Date: 2021/4/16 10:37
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public String createOrder(Order order) {
        orderService.createOrder(order);
        return "SUCCESS";
    }
}
