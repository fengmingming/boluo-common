package boluo.common.cache.aop;


import boluo.common.cache.CacheManager;
import boluo.common.cache.annotation.L1Cache;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class L1CacheInterceptor extends AbstractCacheInterceptor {

    private final ConcurrentHashMap<Method, CacheHolder> holderMap = new ConcurrentHashMap<>();

    public L1CacheInterceptor(ApplicationContext context) {
        super(context);
    }

    @Override
    protected CacheHolder buildCacheHolder(Method m) {
        CacheHolder holder = holderMap.get(m);
        if(holder == null) {
            L1Cache l1Cache = AnnotationUtils.findAnnotation(m, L1Cache.class);
            if(l1Cache != null) {
                holder = holderMap.computeIfAbsent(m, key -> {
                    return CacheHolder.builder().cache(CacheManager.buildL1Cache(buildCacheConfig(l1Cache, m))).key(l1Cache.key()).build();
                });
            }
        }
        return holder;
    }

}
