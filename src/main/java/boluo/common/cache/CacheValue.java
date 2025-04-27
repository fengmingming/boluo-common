package boluo.common.cache;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CacheValue {

    public long time;
    private Object value;

}
