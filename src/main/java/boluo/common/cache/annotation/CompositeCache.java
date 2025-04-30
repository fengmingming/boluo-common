package boluo.common.cache.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CompositeCache {

    String name() default "";

    String key() default "";

    L1Cache l1Cache();

    L2Cache l2Cache();

}
