package boluo.common.cache;

import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

//redis缓存
public class L2Cache extends AbstractCache implements Cache{

    private final Function<Object, String> keyConverter;
    private final RedissonClient redissonClient;
    private final Codec codec = new JsonJacksonCodec();

    public L2Cache(L2CacheConfig cacheConfig) {
        super(cacheConfig);
        Objects.requireNonNull(cacheConfig.getRedissonClient(), "redissonClient is null in l2cache config");
        Objects.requireNonNull(cacheConfig.getKeyConverter(), "keyConverter is null in l2cache config");
        this.redissonClient = cacheConfig.getRedissonClient();
        this.keyConverter = cacheConfig.getKeyConverter();
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
                value = bucket.get();
                if(value == null) {
                    Object v = supplier.get();
                    if(v != null || getCacheConfig().isCacheNullValue()) {
                        value = buildCacheValue(v);
                        bucket.set(value, Duration.ofMillis(getCacheConfig().getTimeUnit().toMillis(getCacheConfig().getExpireTime())));
                        return value.getValue();
                    }else {
                        return null;
                    }
                }
            }finally {
                lock.unlock();
            }
        }
        if(needRefresh(value.getTime())) {
            RLock lock = redissonClient.getLock(String.format("%s:lock", redisKey));
            boolean executable;
            try {
                executable = lock.tryLock(0, getCacheConfig().getExpireTime(), getCacheConfig().getTimeUnit());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(executable) {
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
            return String.format("%s:%s", getCacheConfig().getName(), keyConverter.apply(key));
        }
    }

}
