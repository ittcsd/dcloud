package com.dcloud.dependencies.utlils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.lang.Nullable;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author dcloud
 * @date 2021/12/28 20:19
 */
@Slf4j
public class RedisUtil {

    private static final String LOCK_SCRIPT_PATH = "lua/lock.lua";
    private static final String UNLOCK_SCRIPT_PATH = "lua/unLock.lua";

    private RedisTemplate<String, Object> redisTemplate;
    
    

    public RedisUtil(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

// =============================common============================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }
    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值g
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }
    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hgetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 通过key 和 fields 获取指定的value 如果没有对应的value则返回null
     *
     * @param key
     * @param collection
     * @return
     */
    public List<Object> hmget(String key, List<Object> collection) {
        return redisTemplate.opsForHash().multiGet(key, collection);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key
     * @param item
     * @param by
     * @return
     */
    public Long hincr(String key, String item, long by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public Long hdecr(String key, String item, long by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    /**
     * hash大小
     *
     * @param key
     * @return
     */
    public Long hsize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 获取全部values
     *
     * @param key
     * @return
     */
    public List<Object> hvals(String key) {
        return redisTemplate.opsForHash().values(key);
    }
    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return 0;
        }
    }
    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return 509
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ;
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Removes and returns first element in list stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return can be {@literal null}.
     * @see <a href="https://redis.io/commands/lpop">Redis Documentation: LPOP</a>
     */
    @Nullable
    public Object leftPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Removes and returns first element from lists stored at {@code key} . <br>
     * <b>Blocks connection</b> until element available or {@code timeout} reached.
     *
     * @param key must not be {@literal null}.
     * @param timeout
     * @param unit must not be {@literal null}.
     * @return can be {@literal null}.
     * @see <a href="https://redis.io/commands/blpop">Redis Documentation: BLPOP</a>
     */
    @Nullable
    public Object leftPop(String key, long timeout, TimeUnit unit) {
        try {
            return redisTemplate.opsForList().leftPop(key, timeout, unit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    // =============================zset==================================

    /**
     * 向有序集合添加一个或多个成员，或者更新已存在成员的分数
     *
     * @param key   键
     * @param value 成员
     * @param score 成员分数
     * @return
     */
    public boolean zadd(String key, Object value, double score) {
        try {
            redisTemplate.opsForZSet().add(key, value, score);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Remove values from sorted set. Return number of removed elements.
     *
     * @param key
     * @param values
     * @return
     */
    public Long zRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 有序集合中对指定成员的分数加上增量 increment
     *
     * @param key       键
     * @param value     成员
     * @param incrScore 指定增加的分数
     * @return
     */
    public Double zincrby(String key, Object value, double incrScore) {
        try {
            return redisTemplate.opsForZSet().incrementScore(key, value, incrScore);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Determine the index of element with value in a sorted set.
     *
     * @param key
     * @param o
     * @return
     */
    public Long zRank(String key, Object o) {
        try {
            return redisTemplate.opsForZSet().rank(key, o);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Determine the index of element with {@code value} in a sorted set when scored high to low.
     *
     * @param key must not be {@literal null}.
     * @param o   the value.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zrevrank">Redis Documentation: ZREVRANK</a>
     */
    @Nullable
    public Long zReverseRank(String key, Object o) {
        try {
            return redisTemplate.opsForZSet().reverseRank(key, o);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get elements between {@code start} and {@code end} from sorted set.
     *
     * @param key   must not be {@literal null}.
     * @param start
     * @param end
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zrange">Redis Documentation: ZRANGE</a>
     */
    @Nullable
    public Set<Object> zRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get set of {@link RedisZSetCommands.Tuple}s between {@code start} and {@code end} from sorted set.
     *
     * @param key   must not be {@literal null}.
     * @param start
     * @param end
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zrange">Redis Documentation: ZRANGE</a>
     */
    @Nullable
    public Set<ZSetOperations.TypedTuple<Object>> zRangeWithScores(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get elements where score is between {@code min} and {@code max} from sorted set.
     *
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
     */
    @Nullable
    public Set<Object> zRangeByScore(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get set of {@link RedisZSetCommands.Tuple}s where score is between {@code min} and {@code max} from sorted set.
     *
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
     */
    @Nullable
    public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get elements in range from {@code start} to {@code end} where score is between {@code min} and {@code max} from
     * sorted set.
     *
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
     */
    @Nullable
    public Set<Object> zRangeByScore(String key, double min, double max, long offset, long count) {
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 返回有序集中指定区间内的成员，通过索引，分数从高到低
     *
     * @param key   键
     * @param start 起始位置
     * @param stop  结束位置
     * @return
     */
    public Set<Object> zReverseRange(String key, long start, long stop) {
        try {
            Set<Object> result = redisTemplate.opsForZSet().reverseRange(key, start, stop);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }


    private static final Long SUCCESS = 1L;

    /**
     * 获取分布式锁
     *
     * @param lockKey     锁
     * @param requestId   请求标识
     * @param expireTime  单位秒
     * @param waitTimeout 单位毫秒
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, int expireTime, long waitTimeout) {
        // 当前时间
        long nanoTime = System.nanoTime();
        try {
            do {
                DefaultRedisScript redisLockScript = this.getRedisScript(LOCK_SCRIPT_PATH);
                Object result = redisTemplate.execute(redisLockScript, Collections.singletonList(lockKey), requestId, expireTime);
                if (SUCCESS.equals(result)) {
                    return true;
                }
                //休眠50毫秒
                Thread.sleep(50L);
            } while ((System.nanoTime() - nanoTime) < TimeUnit.MILLISECONDS.toNanos(waitTimeout));
        } catch (Exception e) {
            log.error("尝试获取分布式锁-key[" + lockKey + "]异常", e);
        }
        return false;
    }

    private DefaultRedisScript getRedisScript(String path) {
        DefaultRedisScript redisScript = new DefaultRedisScript();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(path)));
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    /**
     * 释放锁
     *
     * @param lockKey   锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {
        DefaultRedisScript redisUnLockScript = this.getRedisScript(UNLOCK_SCRIPT_PATH);
        Object result = redisTemplate.execute(redisUnLockScript, Collections.singletonList(lockKey), requestId);
        return SUCCESS.equals(result);
    }

//    public Set<String> scan(String key) {
//        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
//            Set<String> keys = Sets.newHashSet();
//            RedisAsyncCommandsImpl commands = (RedisAsyncCommandsImpl) connection.getNativeConnection();
//            MultiKeyCommands multiKeyCommands = (MultiKeyCommands) commands;
//            ScanParams scanParams = new ScanParams();
//            scanParams.match("*" + key + "*");
//            scanParams.count(1000);
//            ScanResult<String> scan = multiKeyCommands.scan("0", scanParams);
//            while (null != scan.getCursor()) {
//                keys.addAll(scan.getResult());
//                if (!StringUtils.equals("0", scan.getCursor())) {
//                    scan = multiKeyCommands.scan(scan.getCursor(), scanParams);
//                } else {
//                    break;
//                }
//            }
//
//            return keys;
//        });
//    }

    /**
     * scan 实现
     *
     * @param pattern  表达式
     * @param consumer 对迭代到的key进行操作
     */
    public void scan(String pattern, Consumer<byte[]> consumer) {
        this.redisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern).build())) {
                cursor.forEachRemaining(consumer);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 获取符合条件的key
     *
     * @param pattern 表达式
     * @return
     */
    public List<String> keys(String pattern) {
        List<String> keys = new ArrayList<>();
        this.scan(pattern, item -> {
            //符合条件的key
            String key = new String(item, StandardCharsets.UTF_8);
            keys.add(key);
        });
        return keys;
    }

    /**
     * hyperLogLog add
     *
     * @param key
     * @param value
     * @return
     */
    public long pfAdd(String key, Object... value) {
        try {
            return redisTemplate.opsForHyperLogLog().add(key, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    public long pfCount(String key) {
        try {
            return redisTemplate.opsForHyperLogLog().size(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 通过区间获取zset中的成员
     *
     * @param key   键
     * @param start 起始索引
     * @param stop  结束索引
     * @return 成员集合
     */
    public Set<ZSetOperations.TypedTuple<Object>> zRevRangeWithScore(String key, long start, long stop) {
        try {
            return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, stop);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 返回zset中指定成员的索引(分数倒序)
     *
     * @param key
     * @param member
     * @return
     */
    public Long zRevRank(String key, Object member) {
        try {
            return redisTemplate.opsForZSet().reverseRank(key, member);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * @param key key
     * @return zset的成员数量
     */
    public Long zCard(String key) {
        try {
            return redisTemplate.opsForZSet().zCard(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * zset删除成员
     *
     * @param key
     * @param member
     * @return
     */
    public Long zRem(String key, Object member) {
        try {
            return redisTemplate.opsForZSet().remove(key, member);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public Double zScore(String key, Object member) {
        try {
            return redisTemplate.opsForZSet().score(key, member);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public boolean setIfAbsent(String key, String value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }
    
    
    
    
    public long incrementAndGet(String key) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory()); 
		return counter.incrementAndGet();
	}	
    
    
    public long atomicGet(String key) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory()); 
		return counter.get();
	}	
    
    

}