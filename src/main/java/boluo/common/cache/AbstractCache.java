package boluo.common.cache;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public abstract class AbstractCache {

    private final CacheConfig cacheConfig;

    public AbstractCache(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
        if(this.cacheConfig.getKeyGenerator() == null) {
            this.cacheConfig.setKeyGenerator(new DefaultKeyGenerator());
        }
    }

    protected CacheValue buildCacheValue(Object value) {
        CacheValue wrapValue = new CacheValue();
        wrapValue.setTime(System.currentTimeMillis());
        wrapValue.setValue(value);
        return wrapValue;
    }

    protected Object generate(Object key) {
        key = this.cacheConfig.getKeyGenerator().generate(key);
        return Objects.requireNonNull(key, "cache key is null");
    }

    protected CacheConfig getCacheConfig() {
        return this.cacheConfig;
    }

    protected boolean needRefresh(long time) {
        int refreshTime = getCacheConfig().getRefreshTime();
        TimeUnit timeUnit = getCacheConfig().getTimeUnit();
        return (refreshTime > 0 && System.currentTimeMillis() - time > timeUnit.toMillis(refreshTime));
    }

}
