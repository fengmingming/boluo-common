package boluo.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Supplier;

// 本地缓存
public class L1Cache extends AbstractCache implements Cache {

    private final com.github.benmanes.caffeine.cache.Cache<Object, CacheValue> cache;
    private final ConcurrentSkipListSet<Object> refreshLock = new ConcurrentSkipListSet<>();

    public L1Cache(CacheConfig cacheConfig) {
        super(cacheConfig);
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfig.getExpireTime(), cacheConfig.getTimeUnit())
                .maximumSize(cacheConfig.getLimit())
                .recordStats()
                .build();
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
            if(refreshLock.add(wrapKey)) {
                try {
                    Object v = supplier.get();
                    if(v != null || getCacheConfig().isCacheNullValue()) {
                        value = buildCacheValue(v);
                        this.cache.put(wrapKey, value);
                    }
                }finally {
                    refreshLock.remove(wrapKey);
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
