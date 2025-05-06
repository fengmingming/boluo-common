package boluo.common.cache.aop;


import boluo.common.cache.CacheConfig;
import boluo.common.cache.CacheManager;
import boluo.common.cache.L2CacheConfig;
import boluo.common.cache.annotation.CompositeCache;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class CompositeCacheInterceptor extends AbstractCacheInterceptor {

    private final ConcurrentHashMap<Method, CacheHolder> holderMap = new ConcurrentHashMap<>();

    public CompositeCacheInterceptor(ApplicationContext context) {
        super(context);
    }

    @Override
    protected CacheHolder buildCacheHolder(Method m) {
        CacheHolder holder = holderMap.get(m);
        if(holder == null) {
            CompositeCache cacheA = AnnotationUtils.findAnnotation(m, CompositeCache.class);
            holder = holderMap.computeIfAbsent(m, key -> {
                String name = cacheA.name();
                if(!StringUtils.hasText(name)) {
                    name = generateCacheName(m);
                }
                CacheConfig l1CacheConfig = buildCacheConfig(cacheA.l1Cache(), m);
                if(!StringUtils.hasText(cacheA.l1Cache().name())) {
                    l1CacheConfig.setName(name + ":l1");
                }
                L2CacheConfig l2CacheConfig = buildCacheConfig(cacheA.l2Cache(), m);
                if(!StringUtils.hasText(cacheA.l2Cache().name())) {
                    l2CacheConfig.setName(name + ":l2");
                }
                return CacheHolder.builder().cache(CacheManager.buildCompositeCache(name, l1CacheConfig, l2CacheConfig)).key(cacheA.key()).build();
            });
        }
        return holder;
    }

}
