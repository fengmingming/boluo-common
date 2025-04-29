package boluo.common.cache;

import boluo.common.cache.annotation.CompositeCache;
import boluo.common.cache.annotation.L1Cache;
import boluo.common.cache.annotation.L2Cache;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

public class CacheProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        boolean proxy = false;
        for(Method method : bean.getClass().getMethods()) {
            if(AnnotationUtils.findAnnotation(method, CompositeCache.class) != null
                    || AnnotationUtils.findAnnotation(method, L2Cache.class) != null
                    || AnnotationUtils.findAnnotation(method, L1Cache.class) != null) {
                proxy = true;
                break;
            }
        }
        if(proxy) {
            ProxyFactory proxyFactory = new ProxyFactory(bean);
            proxyFactory.addAdvice(new CacheInterceptor());
            proxyFactory.setProxyTargetClass(true);
            return proxyFactory.getProxy();
        }else {
            return bean;
        }
    }

}
