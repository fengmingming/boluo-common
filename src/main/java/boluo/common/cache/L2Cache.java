package boluo.common.cache;

import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;

import java.time.Duration;
import java.util.function.Supplier;

//redis缓存
public class L2Cache extends AbstractCache implements Cache{

    private final RedissonClient redissonClient;
    private final KeyGenerator keyGenerator = new DefaultKeyGenerator();
    private final Codec codec = new JsonJacksonCodec();

    public L2Cache(RedissonClient redissonClient, CacheConfig cacheConfig) {
        super(cacheConfig);
        this.redissonClient = redissonClient;
    }

    @Override
    public void put(Object key, Object value) {
        if(value != null || getCacheConfig().isCacheNullValue()) {
            String redisKey = generateRedisKey(key);
            RBucket<CacheValue> bucket = redissonClient.getBucket(redisKey, codec);
            bucket.set(buildCacheValue(value), Duration.ofMillis(getCacheConfig().getTimeUnit().toMillis(getCacheConfig().getExpireTime())));
        }
    }

    @Override
    public Object get(Object key) {
        String redisKey = generateRedisKey(key);
        RBucket<CacheValue> bucket = redissonClient.getBucket(redisKey, codec);
        CacheValue value = bucket.get();
        if(value == null) return null;
        return value.getValue();
    }

    @Override
    public Object get(Object key, Supplier<Object> supplier) {
        String redisKey = generateRedisKey(key);
        RBucket<CacheValue> bucket = redissonClient.getBucket(redisKey, codec);
        CacheValue value = bucket.get();
        if(value == null) {
            RLock lock = redissonClient.getLock(String.format("%s:lock", redisKey));
            lock.lock(getCacheConfig().getExpireTime(), getCacheConfig().getTimeUnit());
            try{
                Object v = supplier.get();
                if(v != null || getCacheConfig().isCacheNullValue()) {
                    value = buildCacheValue(v);
                    bucket.set(value, Duration.ofMillis(getCacheConfig().getTimeUnit().toMillis(getCacheConfig().getExpireTime())));
                    return value.getValue();
                }else {
                    return null;
                }
            }finally {
                lock.unlock();
            }
        }
        if(needRefresh(value.getTime())) {
            RLock lock = redissonClient.getLock(String.format("%s:lock", redisKey));
            if(lock.tryLock()) {
                try{
                    Object v = supplier.get();
                    if(v != null || getCacheConfig().isCacheNullValue()) {
                        value = buildCacheValue(v);
                        bucket.set(value, Duration.ofMillis(getCacheConfig().getTimeUnit().toMillis(getCacheConfig().getExpireTime())));
                    }
                }finally {
                    lock.unlock();
                }
            }
        }
        return value.getValue();
    }

    @Override
    public void remove(Object key) {
        String redisKey = generateRedisKey(key);
        RBucket<CacheValue> bucket = redissonClient.getBucket(redisKey, codec);
        bucket.delete();
    }

    private String generateRedisKey(Object key) {
        key = super.generate(key);
        if(key instanceof String) {
            return String.format("%s:%s", getCacheConfig().getName(), key);
        }else {
            return String.format("%s:%s", getCacheConfig().getName(), keyGenerator.generate(key));
        }
    }

}
