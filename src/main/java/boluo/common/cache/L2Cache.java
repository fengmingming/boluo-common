package boluo.common.cache;

import org.redisson.api.RedissonClient;

import java.util.function.Supplier;

//redis缓存
public class L2Cache implements Cache{

    private final RedissonClient redissonClient;
    private final CacheConfig cacheConfig;

    public L2Cache(RedissonClient redissonClient, CacheConfig cacheConfig) {
        this.redissonClient = redissonClient;
        this.cacheConfig = cacheConfig;
    }


    @Override
    public void put(Object key, Object value) {

    }

    @Override
    public Object get(Object key) {
        return null;
    }

    @Override
    public Object get(Object key, Supplier<Object> supplier) {
        return null;
    }

    @Override
    public void remove(Object key) {

    }

}
