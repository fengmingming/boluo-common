package boluo.common.cache;

import java.util.Objects;
import java.util.function.Supplier;

public class CompositeCache implements Cache{

    private final String name;
    private final Cache l1Cache;
    private final Cache l2Cache;

    public CompositeCache(String name, Cache l1Cache, Cache l2Cache) {
        this.name = name;
        this.l1Cache = Objects.requireNonNull(l1Cache, "l1Cache is null");
        this.l2Cache = Objects.requireNonNull(l2Cache, "l2Cache is null");
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void put(Object key, Object value) {
        l1Cache.put(key, value);
        l2Cache.put(key, value);
    }

    @Override
    public Object get(Object key) {
        Object value = l1Cache.get(key);
        if(value == null) {
            value = l2Cache.get(key);
        }
        return value;
    }

    @Override
    public Object get(Object key, Supplier<Object> supplier) {
        return l1Cache.get(key, () -> l2Cache.get(key, supplier));
    }

    @Override
    public void remove(Object key) {
        l2Cache.remove(key);
        l1Cache.remove(key);
    }

}
