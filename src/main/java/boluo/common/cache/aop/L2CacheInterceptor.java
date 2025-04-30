package boluo.common.cache.aop;


import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

public class L2CacheInterceptor extends AbstractCacheInterceptor {

    public L2CacheInterceptor(ApplicationContext context) {
        super(context);
    }

    @Override
    protected CacheHolder buildCacheConfig(Method m) {
        return null;
    }

}
