package boluo.common.cache;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

@Setter
@Getter
public class CacheConfig {

    private boolean enable;
    private int refreshTime;
    private int expireTime;
    private int limit = 1024;
    private TimeUnit timeUnit;
    private KeyGenerator keyGenerator;
    private ValueEncoder valueEncoder;
    private ValueDecoder valueDecoder;

}
