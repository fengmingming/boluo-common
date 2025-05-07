package boluo.common.cache.annotation;

import boluo.common.cache.aop.CacheSelector;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(CacheSelector.class)
public @interface EnableBoluoCache {

    int order() default Ordered.HIGHEST_PRECEDENCE;

}
