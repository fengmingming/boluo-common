package boluo.common.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "boluo.cache.enable", havingValue = "true", matchIfMissing = true)
public class CacheConfiguration {

    @Bean
    public CacheProcessor cacheProcessor() {
        return new CacheProcessor();
    }

}
