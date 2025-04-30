package boluo.common.cache;

public class DefaultKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object key) {
        return key;
    }

}
