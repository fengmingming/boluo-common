package boluo.common.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultKeyConverter implements KeyConverter{

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String apply(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
