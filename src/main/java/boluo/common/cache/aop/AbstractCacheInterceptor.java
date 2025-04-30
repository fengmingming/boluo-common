package boluo.common.cache.aop;

import boluo.common.cache.KeyGenerator;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Map;

public abstract class AbstractCacheInterceptor implements MethodInterceptor {

    protected final ApplicationContext context;

    public AbstractCacheInterceptor(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        CacheHolder holder = buildCacheConfig(invocation.getMethod());
        if(holder == null) {
            return invocation.proceed();
        }
        Object key = computeKey(holder.getKey(), invocation.getMethod(), invocation.getArguments());
        return holder.getCache().get(key, () -> {
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

    protected String buildCacheName(Method m) {
        return null;
    }

    protected KeyGenerator buildKeyGenerator(Class<? extends KeyGenerator> clazz) {
        Map<String, ?> map = context.getBeansOfType(clazz);
        if(!map.values().isEmpty()) {
            return (KeyGenerator) map.values().stream().findFirst().get();
        }else {
            try {
                return clazz.getConstructor().newInstance();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected abstract CacheHolder buildCacheConfig(Method m);

}
