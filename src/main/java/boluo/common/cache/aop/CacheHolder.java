package boluo.common.cache.aop;

import boluo.common.cache.Cache;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CacheHolder {

    private Cache cache;
    private String key;

}
