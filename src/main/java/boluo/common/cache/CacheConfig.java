package boluo.common.cache;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

@Setter
@Getter
@Builder
public class CacheConfig {

    private String name;
    private int refreshTime = 10 * 1000;
    private int expireTime = 60 * 1000;
    private int limit = 1024;
    private boolean cacheNullValue = true;
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    private KeyGenerator keyGenerator;
    private RedissonClient redissonClient;

}
