package boluo.common.cache.annotation;

import boluo.common.cache.DefaultKeyGenerator;
import boluo.common.cache.KeyGenerator;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface L1Cache {

    String name() default "";

    String key() default "";

    int refreshTime() default 10 * 1000;

    int expireTime() default 60 * 1000;

    int limit() default 1024;

    boolean cacheNullValue() default true;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    Class<? extends KeyGenerator> keyGenerator() default DefaultKeyGenerator.class;

}
