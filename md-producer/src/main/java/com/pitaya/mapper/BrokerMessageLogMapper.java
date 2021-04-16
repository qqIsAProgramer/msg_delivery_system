package com.pitaya.mapper;

import com.pitaya.model.BrokerMessageLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Author: qyl
 * @Date: 2021/4/15 21:42
 */
public interface BrokerMessageLogMapper {

    /**
     * 查询消息状态为 0(发送中)，且已经超时的消息集合
     */
    List<BrokerMessageLog> querySendingAndTimeoutMessage();

    /**
     * 将重新发送的 message 进行统计
     */
    void updateRetry(@Param("messageId") String messageId, @Param("updateTime") Date updateTime);

    /**
     * 更新最终消息发送结果
     */
    void updateLogStatus(@Param("messageId") String messageId,
                         @Param("status") String status,
                         @Param("updateTime")Date updateTime);

    int insertLog(BrokerMessageLog log);
}
