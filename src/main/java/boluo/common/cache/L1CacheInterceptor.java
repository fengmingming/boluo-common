package boluo.common.cache;


import boluo.common.cache.annotation.L1Cache;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class L1CacheInterceptor extends AbstractCacheInterceptor implements MethodInterceptor {

    private final ConcurrentHashMap<Method, L1Cache> map = new ConcurrentHashMap<>();

    public L1CacheInterceptor(ApplicationContext context) {
        super(context);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method m = invocation.getMethod();
        Object[] args = invocation.getArguments();
        L1Cache l1Cache = AnnotationUtils.findAnnotation(m, L1Cache.class);
        if(l1Cache == null) {
            return invocation.proceed();
        }
        String name = l1Cache.name();
        String key = computeKey(l1Cache.key(), m, args);
        Cache cache = CacheManager.getCache(name);
        if(cache == null) {
            Class<? extends KeyGenerator> clazz = l1Cache.keyGenerator();
            KeyGenerator keyGenerator = context.getBean(clazz);
            if(keyGenerator == null) {
                keyGenerator = clazz.getConstructor().newInstance();
            }
            cache = CacheManager.buildL1Cache(CacheConfig.builder()
                    .name(name).refreshTime(l1Cache.refreshTime()).expireTime(l1Cache.expireTime())
                    .limit(l1Cache.limit()).cacheNullValue(l1Cache.cacheNullValue()).timeUnit(l1Cache.timeUnit())
                    .keyGenerator(keyGenerator).build());
        }
        return cache.get(key, () -> {
            try {
                return invocation.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected String computeKey(String key, Method m, Object[] args) {
        return key;
    }

}
