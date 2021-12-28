--
-- Created by IntelliJ IDEA.
-- User: zhangkai
-- Date: 2020/9/30
-- Time: 11:31
-- To change this template use File | Settings | File Templates.
--

if redis.call('get', KEYS[1]) == ARGV[1] then
    return redis.call('del', KEYS[1])
else
    return 0
end