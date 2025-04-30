package boluo.common.cache;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.redisson.api.RedissonClient;

import java.util.function.Function;

@Setter
@Getter
@Builder
public class RedisCacheConfig extends CacheConfig{

    private RedissonClient redissonClient;
    private Function<Object, String> keyConverter;

}
