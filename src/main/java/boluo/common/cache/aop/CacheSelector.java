package boluo.common.cache.aop;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class CacheSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{CachePropertiesRegistrar.class.getName(), CacheConfiguration.class.getName()};
    }

}