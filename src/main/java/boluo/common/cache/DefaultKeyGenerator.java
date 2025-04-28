package boluo.common.cache;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultKeyGenerator implements KeyGenerator {

    private final ObjectMapper om;

    public DefaultKeyGenerator() {
        om = new ObjectMapper();
        om.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    public DefaultKeyGenerator(ObjectMapper om) {
        this.om = om;
    }

    @Override
    public Object generate(Object key) {
        try {
            return om.writeValueAsString(key);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
