package com.pitaya.utils;

import java.util.Date;

/**
 * @Author: qyl
 * @Date: 2021/4/15 18:58
 */
public class DateUtil {

    public static Date setTimeout(Date orderTime, int timeout) {
        return new Date(orderTime.getTime() + 1000 * timeout);
    }
}
