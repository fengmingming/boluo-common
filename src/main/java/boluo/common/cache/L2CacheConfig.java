package boluo.common.cache;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.redisson.api.RedissonClient;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class L2CacheConfig extends CacheConfig{

    private RedissonClient redissonClient;
    private KeyConverter keyConverter;

}
