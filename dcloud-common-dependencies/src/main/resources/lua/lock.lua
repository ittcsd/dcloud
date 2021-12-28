--
-- Created by IntelliJ IDEA.
-- User: zhangkai
-- Date: 2020/9/30
-- Time: 11:16
-- To change this template use File | Settings | File Templates.
--
-- KEYS[1]-lockKey, ARGV[1]--requestId, ARGV[2]--expire
if redis.call('setNx', KEYS[1], ARGV[1]) == 1 then
    if tonumber(ARGV[2]) > 0 then
        redis.call('expire', KEYS[1], ARGV[2])
    end
    return 1
else
    if redis.call('get', KEYS[1]) == ARGV[1] then
        if tonumber(ARGV[2]) > 0 then
            redis.call('expire', KEYS[1], ARGV[2])
        end
        return 1
    else
        return 0
    end
end