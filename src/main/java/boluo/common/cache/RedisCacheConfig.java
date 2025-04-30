package boluo.common.cache;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.redisson.api.RedissonClient;

import java.util.function.Function;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class RedisCacheConfig extends CacheConfig{

    private RedissonClient redissonClient;
    private Function<Object, String> keyConverter;

}
