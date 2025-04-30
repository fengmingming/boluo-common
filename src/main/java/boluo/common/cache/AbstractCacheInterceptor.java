package boluo.common.cache;

import org.springframework.context.ApplicationContext;

public class AbstractCacheInterceptor {

    protected final ApplicationContext context;

    public AbstractCacheInterceptor(ApplicationContext context) {
        this.context = context;
    }

}
