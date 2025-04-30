package boluo.common.cache.annotation;

import boluo.common.cache.DefaultKeyGenerator;
import boluo.common.cache.KeyGenerator;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface L2Cache {

    String name() default "default";

    String key();

    int refreshTime() default 10 * 1000;

    int expireTime() default 60 * 1000;

    boolean cacheNullValue() default true;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    Class<? extends KeyGenerator> keyGenerator() default DefaultKeyGenerator.class;

}
