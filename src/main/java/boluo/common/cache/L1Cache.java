package boluo.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.function.Supplier;

// 本地缓存
public class L1Cache implements Cache {

    private final CacheConfig cacheConfig;
    private final com.github.benmanes.caffeine.cache.Cache cache;

    public L1Cache(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
        this.cache = Caffeine.newBuilder().build();
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
