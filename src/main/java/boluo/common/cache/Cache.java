package boluo.common.cache;

import java.util.function.Supplier;

public interface Cache {

    public String getName();

    public void put(Object key, Object value);

    public Object get(Object key);

    public Object get(Object key, Supplier<Object> supplier);

    public void remove(Object key);

}
