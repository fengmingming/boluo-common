package boluo.common.cache.aop;

import boluo.common.cache.CacheManager;
import boluo.common.cache.annotation.L2Cache;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class L2CacheInterceptor extends AbstractCacheInterceptor {

    private final ConcurrentHashMap<Method, CacheHolder> holderMap = new ConcurrentHashMap<>();

    public L2CacheInterceptor(ApplicationContext context) {
        super(context);
    }

    @Override
    protected CacheHolder buildCacheHolder(Method m) {
        CacheHolder holder = holderMap.get(m);
        if(holder == null) {
            L2Cache l2Cache = AnnotationUtils.findAnnotation(m, L2Cache.class);
            if(l2Cache != null) {
                holder = holderMap.computeIfAbsent(m, key -> {
                    return CacheHolder.builder().cache(CacheManager.buildL2Cache(buildCacheConfig(l2Cache, m))).key(l2Cache.key()).build();
                });
            }
        }
        return holder;
    }

}
