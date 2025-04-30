package boluo.common.cache;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {

    private static final ConcurrentHashMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

    public static Cache getCache(String name) {
        Objects.requireNonNull(name, "cache name is null in cache config");
        return cacheMap.get(name);
    }

    public static Cache buildL1Cache(CacheConfig config) {
        Objects.requireNonNull(config, "cache config is null in cache config");
        Cache cache = getCache(config.getName());
        if(cache == null) {
            return cacheMap.computeIfAbsent(config.getName(), key -> new L1Cache(config));
        }else {
            return cache;
        }
    }

    public static Cache buildL2Cache(RedisCacheConfig config) {
        Objects.requireNonNull(config, "cache config is null in cache config");
        Cache cache = getCache(config.getName());
        if(cache == null) {
            return cacheMap.computeIfAbsent(config.getName(), key -> new L2Cache(config));
        }else {
            return cache;
        }
    }

    public static Cache buildCompositeCache(String name, CacheConfig l1Config, RedisCacheConfig l2Config) {
        Cache cache = getCache(name);
        if(cache == null) {
            return cacheMap.computeIfAbsent(name, key -> new CompositeCache(buildL1Cache(l1Config), buildL2Cache(l2Config)));
        }else {
            return cache;
        }
    }

}
