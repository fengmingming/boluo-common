package boluo.common.cache;

import boluo.common.cache.annotation.CompositeCache;
import boluo.common.cache.annotation.L1Cache;
import boluo.common.cache.annotation.L2Cache;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@ConditionalOnProperty(value = "boluo.cache.enable", havingValue = "true", matchIfMissing = true)
public class CacheConfiguration {

    @Bean(name = "boluo.l1CacheAdvisor")
    public Advisor l1CacheAdvisor(ApplicationContext context) {
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(new AnnotationMatchingPointcut(null, L1Cache.class, true));
        advisor.setAdvice(new L1CacheInterceptor(context));
        advisor.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return advisor;
    }

    @Bean(name = "boluo.l2CacheAdvisor")
    public Advisor l2CacheAdvisor() {
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(new AnnotationMatchingPointcut(null, L2Cache.class, true));
        advisor.setAdvice(new L2CacheInterceptor());
        advisor.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        return advisor;
    }

    @Bean(name = "boluo.compositeCacheAdvisor")
    public Advisor compositeCacheAdvisor() {
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(new AnnotationMatchingPointcut(null, CompositeCache.class, true));
        advisor.setAdvice(new CompositeCacheInterceptor());
        advisor.setOrder(Ordered.HIGHEST_PRECEDENCE + 3);
        return advisor;
    }

}
