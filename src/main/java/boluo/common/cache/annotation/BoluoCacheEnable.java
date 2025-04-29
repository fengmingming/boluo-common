package boluo.common.cache.annotation;

import boluo.common.cache.CacheConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Import(CacheConfiguration.class)
public @interface BoluoCacheEnable {
}
