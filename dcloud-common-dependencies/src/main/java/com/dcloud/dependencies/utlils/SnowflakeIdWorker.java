package com.dcloud.dependencies.utlils;

import com.dcloud.dependencies.config.SnowFlakeProperties;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Twitter_Snowflake<br>
 * SnowFlake的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId<br>
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 *
 * @author dcloud
 * @date 2021/12/28 20:19
 */
@Slf4j
public class SnowflakeIdWorker {

    /**
     * 起始的时间戳
     */
    private final static long START_STMP = 1480166465631L;

    /**
     * 每一部分占用的位数
     */
    /**
     * 序列号占用的位数
     */
    private final static long SEQUENCE_BIT = 12;
    /**
     * 机器标识占用的位数
     */
    private final static long MACHINE_BIT = 5;
    /**
     * 数据中心占用的位数
     */
    private final static long DATA_CENTER_BIT = 5;

    /** 支持的最大数据标识id，结果是31 */
    private final static long MAX_DATA_CENTER_NUM = -1L ^ (-1L << DATA_CENTER_BIT);
    /** 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /** 机器ID向左移12位 */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    /** 数据标识id向左移17位(12+5) */
    private final static long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    /** 时间截向左移22位(5+5+12) */
    private final static long TIMESTMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;

    /**
     * 数据中心
     */
    private long dataCenterId;
    /**
     * 机器标识
     */
    private long machineId;
    /**
     * 序列号
     */
    private long sequence = 0L;
    /**
     * 上一次时间戳
     */
    private long lastStmp = -1L;

    private volatile Integer state = 0;

    private static final Long MAX_WORKER_ID = 1023L;

    private SnowFlakeProperties snowFlakeProperties;

    private RedisUtil redisUtil;

    public SnowflakeIdWorker(SnowFlakeProperties snowFlakeProperties, RedisUtil redisUtil) {
        this.snowFlakeProperties = snowFlakeProperties;
        this.redisUtil = redisUtil;
    }

    /**
     * 构造函数
     *
     * @param dataCenterId 数据中心ID (0~31)
     * @param machineId    工作ID (0~31)
     */
    public SnowflakeIdWorker(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("dataCenterId can't be greater than MAX_DATA_CENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    /**
     * 产生下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
        if (state == 0) {
            init();
            state = 1;
        }

        long currStmp = getNewstmp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastStmp = currStmp;

        /**
         * 时间戳部分 | 数据中心部分 | 机器标识部分 | 序列号部分
         */
        return (currStmp - START_STMP) << TIMESTMP_LEFT
                | dataCenterId << DATA_CENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
    }

    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private long getNewstmp() {
        return System.currentTimeMillis();
    }

    public long getDataCenterId() {
        return dataCenterId;
    }

    public void setDataCenterId(long dataCenterId) {
        this.dataCenterId = dataCenterId;
    }

    public long getMachineId() {
        return machineId;
    }

    public void setMachineId(long machineId) {
        this.machineId = machineId;
    }

    private void init() {
        String key = Joiner.on(":").join("SNOW:FLAKE", snowFlakeProperties.getServiceKey(), "WORKER_ID");
        String lockKey = Joiner.on(":").join("SNOW:FLAKE:LOCK", snowFlakeProperties.getServiceKey());

        Long workerId = null;
        do {
            String requestId = PKUtil.createId();
            try {
                Boolean lockFlag = redisUtil.tryLock(lockKey, requestId, 60, 500);
                if (lockFlag) {
                    workerId = redisUtil.incr(key, 1);
                    if (workerId > MAX_WORKER_ID) {
                        redisUtil.set(key, 0);
                        workerId = 0L;
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                redisUtil.releaseLock(lockKey, requestId);
            }
        } while (Objects.isNull(workerId));

        dataCenterId = workerId / 32;
        machineId = workerId % 32;
        log.info("SnowflakeIdWorker init workerId:{}，dataCenterId：{}，machineId：{}", workerId, dataCenterId, machineId);
    }

    //==============================Test=============================================

    /**
     * 测试
     */
    public static void main(String[] args) {
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
        System.out.println(System.currentTimeMillis());
        for (int i = 0; i < (1 << 12); i++) {
            long id = idWorker.nextId();
            System.out.println(Long.toBinaryString(id));
            System.out.println(id);
        }
    }
}
