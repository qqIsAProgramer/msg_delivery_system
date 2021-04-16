package com.pitaya.model;

import lombok.Data;

import java.util.Date;

/**
 * @Author: qyl
 * @Date: 2021/4/15 18:37
 */
@Data
public class BrokerMessageLog {
    /**
     * 消息唯一标识符
     */
    private String messageId;
    /**
     * 消息对应的 JSON 格式
     */
    private String message;
    /**
     * 尝试重新投递消息的次数
     */
    private Integer tryCount;
    /**
     * 消息状态
     * 0:投递中 1:投递成功 2:投递失败
     */
    private String status;
    /**
     * 下一次尝试时间（默认一分钟后）
     */
    private Date nextRetry;
    /**
     * 消息创建时间
     */
    private Date createTime;
    /**
     * 消息更改时间
     */
    private Date updateTime;
}
