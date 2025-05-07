package boluo.common.cache.aop;

import boluo.common.cache.annotation.EnableBoluoCache;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

public class CachePropertiesRegistrar implements ImportBeanDefinitionRegistrar {

    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        String key = "boluo.cache.order";
        if(registry instanceof BeanFactory beanFactory) {
            Environment environment = beanFactory.getBean(Environment.class);
            if(environment instanceof ConfigurableEnvironment env) {
                MutablePropertySources sources = env.getPropertySources();
                Map<String, Object> properties = new HashMap<>();
                if(!env.containsProperty(key)) {
                    MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(EnableBoluoCache.class.getName());
                    Integer order = (Integer) attrs.getFirst("order");
                    properties.put(key, order);
                }
                if(!properties.isEmpty()) {
                    sources.addLast(new MapPropertySource("boluo.cache-properties", properties));
                }
            }
        }
    }

}
