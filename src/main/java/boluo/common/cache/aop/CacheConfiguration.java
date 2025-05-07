package boluo.common.cache.aop;

import boluo.common.cache.annotation.CompositeCache;
import boluo.common.cache.annotation.L1Cache;
import boluo.common.cache.annotation.L2Cache;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.util.NumberUtils;

@Configuration
@ConditionalOnProperty(value = "boluo.cache.enable", havingValue = "true", matchIfMissing = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class CacheConfiguration {

    @Bean(name = "boluo.l1CacheAdvisor")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor l1CacheAdvisor(ApplicationContext context) {
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(new AnnotationMatchingPointcut(null, L1Cache.class, true));
        advisor.setAdvice(new L1CacheInterceptor(context));
        setOrder(advisor, context, 1);
        return advisor;
    }

    @Bean(name = "boluo.l2CacheAdvisor")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor l2CacheAdvisor(ApplicationContext context) {
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(new AnnotationMatchingPointcut(null, L2Cache.class, true));
        advisor.setAdvice(new L2CacheInterceptor(context));
        setOrder(advisor, context, 2);
        return advisor;
    }

    @Bean(name = "boluo.compositeCacheAdvisor")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor compositeCacheAdvisor(ApplicationContext context) {
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(new AnnotationMatchingPointcut(null, CompositeCache.class, true));
        advisor.setAdvice(new CompositeCacheInterceptor(context));
        setOrder(advisor, context, 3);
        return advisor;
    }

    private void setOrder(DefaultPointcutAdvisor advisor, ApplicationContext context, int offset) {
        String key = "boluo.cache.order";
        if(context.getEnvironment().containsProperty(key)) {
            int order = NumberUtils.parseNumber(context.getEnvironment().getProperty(key), Integer.class);
            if(order > Ordered.LOWEST_PRECEDENCE - 3) {
                order = Ordered.LOWEST_PRECEDENCE - 3;
            }
            advisor.setOrder(order + offset);
        }else {
            advisor.setOrder(Ordered.HIGHEST_PRECEDENCE + offset);
        }
    }

}
