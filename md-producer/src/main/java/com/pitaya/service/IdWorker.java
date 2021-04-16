package com.pitaya.service;

/**
 * 分布式自增长ID
 * Twitter 的 Snowflake JAVA实现方案
 * 1|0---0000000000 0000000000 0000000000 0000000000 0---00000---00000---000000000000
 * 64位ID: 1(符号位)+41(时间戳)+5(数据中心)+5(机器标识)+12(序列号)
 */
public class IdWorker {

    /**
     * 起始时间戳
     * 一般取系统的最近时间（一旦确定不能变动）
     */
    private static final long START_TIMESTAMP = 1617324213719L;

    /**
     * 每一部分占用的位数
     */
    private static final long SEQUENCE_BIT = 12;  // 序列号占用的位数
    private static final long MACHINE_BIT = 5;    // 机器标识占用的位数
    private static final long DATACENTER_BIT = 5; // 数据中心占用的位数

    /**
     * 每一部分的最大值，例如：
     * -1 = 11111111111111111111111111111111
     * -1 << 5 = 11111111111111111111111111100000
     * -1 ^ (-1 << 5) = 00000000000000000000000000011111 = ~(-1 << 5)
     */
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT); // 31
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);       // 31
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);         // 4095

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private long datacenterId;    // 数据中心
    private long machineId;       // 机器标识
    private long sequence = 0L;   // 序列号
    private long lastStamp = -1L; // 上一次时间戳

    /**
     * @param datacenterId 数据中心（不超过 31）
     * @param machineId    机器标识（不超过 31）
     */
    public IdWorker(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("Datacenter ID can't be greater than MAX_DATACENTER_NUM or less than 0.");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("Machine ID can't be greater than MAX_MACHINE_NUM or less than 0.");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    /**
     * 生产下一个ID
     * @return ID
     */
    public synchronized long nextId() {
        long currStamp = getNewStamp();
        if (currStamp < lastStamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID.");
        }

        if (currStamp == lastStamp) {
            // 1. 相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0L) {
                // 此时 sequence 加一后已经溢出，加一前已 sequence 达到最大
                currStamp = getNextMillis();
            }
        } else {
            // 2. 不同毫秒内，序列号置为 0
            sequence = 0L;
        }

        lastStamp = currStamp;

        return (currStamp - START_TIMESTAMP) << TIMESTAMP_LEFT // 时间戳部分
                | datacenterId << DATACENTER_LEFT              // 数据中心部分
                | machineId << MACHINE_LEFT                    // 机器标识部分
                | sequence;                                    // 序列号部分
    }


    private long getNextMillis() {
        long millis = getNewStamp();
        // 自旋锁，直到 millis 获取到比 lastStamp 更大的时间戳
        while (millis <= lastStamp) {
            millis = getNewStamp();
        }
        return millis;
    }

    private long getNewStamp() {
        return System.currentTimeMillis();
    }
}
