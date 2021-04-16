package com.pitaya.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: qyl
 * @Date: 2021/4/15 18:28
 */
@Data
public class Order implements Serializable {
    /**
     * 订单ID
     */
    private Long id;
    /**
     * 订单名称
     */
    private String name;
    /**
     * 对应消息的唯一标识符
     */
    private String messageId;
}
