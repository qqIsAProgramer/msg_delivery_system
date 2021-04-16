package com.pitaya.utils;

/**
 * @Author: qyl
 * @Date: 2021/4/15 18:53
 */
public class Constants {
    /**
     * 消息发送状态
     * 0:投递中 1:投递成功 2:投递失败
     */
    public static final String ORDER_SENDING = "0";
    public static final String ORDER_SEND_SUCCESS = "1";
    public static final String ORDER_SEND_FAILURE = "2";

    /**
     * 重新投递超时时间默认60s
     */
    public static final int ORDER_TIMEOUT = 30;
}
