package boluo.common.cache.aop;


import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

public class CompositeCacheInterceptor extends AbstractCacheInterceptor {

    public CompositeCacheInterceptor(ApplicationContext context) {
        super(context);
    }

    @Override
    protected CacheHolder buildCacheConfig(Method m) {
        return null;
    }

}
