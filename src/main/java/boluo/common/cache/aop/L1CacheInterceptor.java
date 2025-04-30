package boluo.common.cache.aop;


import boluo.common.cache.CacheConfig;
import boluo.common.cache.CacheManager;
import boluo.common.cache.KeyGenerator;
import boluo.common.cache.annotation.L1Cache;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class L1CacheInterceptor extends AbstractCacheInterceptor {

    private final ConcurrentHashMap<Method, CacheHolder> holderMap = new ConcurrentHashMap<>();

    public L1CacheInterceptor(ApplicationContext context) {
        super(context);
    }

    @Override
    protected CacheHolder buildCacheConfig(Method m) {
        CacheHolder holder = holderMap.get(m);
        if(holder == null) {
            L1Cache l1Cache = AnnotationUtils.findAnnotation(m, L1Cache.class);
            if(l1Cache != null) {
                holder = holderMap.computeIfAbsent(m, key -> {
                    String name = l1Cache.name();
                    if(!StringUtils.hasText(name)) {
                        name = buildCacheName(m);
                    }
                    KeyGenerator keyGenerator = buildKeyGenerator(l1Cache.keyGenerator());
                    CacheConfig config = CacheConfig.builder().name(name).refreshTime(l1Cache.refreshTime())
                            .expireTime(l1Cache.expireTime()).limit(l1Cache.limit()).timeUnit(l1Cache.timeUnit())
                            .cacheNullValue(l1Cache.cacheNullValue()).keyGenerator(keyGenerator).build();
                    return CacheHolder.builder().cache(CacheManager.buildL1Cache(config)).key(l1Cache.key()).build();
                });
            }
        }
        return holder;
    }

}
