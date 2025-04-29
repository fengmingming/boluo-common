package boluo.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.Striped;

import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

// 本地缓存
public class L1Cache extends AbstractCache implements Cache {

    private final com.github.benmanes.caffeine.cache.Cache<Object, CacheValue> cache;
    private final Striped<Lock> striped;

    public L1Cache(CacheConfig cacheConfig) {
        super(cacheConfig);
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfig.getExpireTime(), cacheConfig.getTimeUnit())
                .maximumSize(cacheConfig.getLimit())
                .recordStats()
                .build();
        this.striped = Striped.lazyWeakLock(cacheConfig.getLimit());
    }

    @Override
    public void put(Object key, Object value) {
        if(value != null || getCacheConfig().isCacheNullValue()) {
            this.cache.put(generate(key), buildCacheValue(value));
        }
    }

    @Override
    public Object get(Object key) {
        CacheValue value = this.cache.getIfPresent(generate(key));
        if(value == null) {
            return null;
        }
        return value.getValue();
    }

    @Override
    public Object get(Object key, Supplier<Object> supplier) {
        final Object wrapKey = generate(key);
        CacheValue value = this.cache.get(wrapKey, k -> {
            Object v = supplier.get();
            if(v != null || getCacheConfig().isCacheNullValue()) {
                return buildCacheValue(v);
            }
            return null;
        });
        if(value == null) {
            return null;
        }
        if(needRefresh(value.getTime())) {
            // refresh
            Lock lock = striped.get(wrapKey);
            if(lock.tryLock()) {
                try {
                    Object v = supplier.get();
                    if(v != null || getCacheConfig().isCacheNullValue()) {
                        value = buildCacheValue(v);
                        this.cache.put(wrapKey, value);
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
        this.cache.invalidate(generate(key));
    }

}
