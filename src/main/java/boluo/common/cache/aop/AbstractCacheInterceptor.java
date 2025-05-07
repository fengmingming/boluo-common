package boluo.common.cache.aop;

import boluo.common.cache.*;
import boluo.common.cache.annotation.L1Cache;
import boluo.common.cache.annotation.L2Cache;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

@Slf4j
public abstract class AbstractCacheInterceptor implements MethodInterceptor {

    protected final ApplicationContext context;

    public AbstractCacheInterceptor(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        CacheHolder holder = buildCacheHolder(invocation.getMethod());
        if(holder == null) {
            return invocation.proceed();
        }
        Object key = generateKey(holder.getKey(), invocation.getMethod(), invocation.getArguments());
        if(log.isDebugEnabled()) {
            log.debug("{} cache interceptor {},{}", holder.getCache().getName(), holder.getKey(), key);
        }
        return holder.getCache().get(key, () -> {
            try {
                return invocation.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected Object generateKey(String key, Method m, Object[] args) {
        if(StringUtils.hasText(key)) {
            SpelExpressionParser parser = new SpelExpressionParser();
            SpelExpression expression = parser.parseRaw(key);
            StandardEvaluationContext context = new StandardEvaluationContext();
            Parameter[] parameters = m.getParameters();
            for(int i = 0,j = m.getParameterCount();i < j;i++) {
                context.setVariable(parameters[i].getName(), args[i]);
            }
            return expression.getValue(context, String.class);
        }else {
            ObjectMapper objectMapper = buildObjectMapper();
            try {
                return objectMapper.writeValueAsString(args);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected String generateCacheName(Method m) {
        return String.format("%s.%s", m.getDeclaringClass().getSimpleName(), m.getName());
    }

    protected KeyGenerator buildKeyGenerator(Class<? extends KeyGenerator> clazz) {
        Map<String, ?> map = context.getBeansOfType(clazz);
        if(!map.values().isEmpty()) {
            return (KeyGenerator) map.values().stream().findFirst().get();
        }else {
            try {
                return clazz.getConstructor().newInstance();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected KeyConverter buildKeyConverter(Class<? extends KeyConverter> clazz) {
        Map<String, ?> map = context.getBeansOfType(clazz);
        if(!map.values().isEmpty()) {
            return (KeyConverter) map.values().stream().findFirst().get();
        }else if(clazz == DefaultKeyConverter.class) {
            return new DefaultKeyConverter(buildObjectMapper());
        }else {
            try {
                return clazz.getConstructor().newInstance();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected RedissonClient buildRedissonClient(String clientBeanName) {
        RedissonClient redissonClient = null;
        Map<String, RedissonClient> map = context.getBeansOfType(RedissonClient.class, true, true);
        if(!map.isEmpty()) {
            redissonClient = map.entrySet().stream().filter(it -> !StringUtils.hasText(clientBeanName) || it.getKey().equals(clientBeanName)).findFirst().map(Map.Entry::getValue).orElseGet(() -> null);
        }
        if(redissonClient == null) {
            log.error("RedissonClient is not found in spring context");
        }
        return redissonClient;
    }

    protected ObjectMapper buildObjectMapper() {
        Map<String, ObjectMapper> map = context.getBeansOfType(ObjectMapper.class, true, true);
        if(!map.isEmpty()) {
            return map.values().stream().findFirst().get();
        }else {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            objectMapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper;
        }
    }

    protected abstract CacheHolder buildCacheHolder(Method m);

    protected CacheConfig buildCacheConfig(L1Cache l1CacheA, Method m) {
        String name = l1CacheA.name();
        if(!StringUtils.hasText(name)) {
            name = generateCacheName(m);
        }
        KeyGenerator keyGenerator = buildKeyGenerator(l1CacheA.keyGenerator());
        return CacheConfig.builder().name(name).refreshTime(l1CacheA.refreshTime())
                .expireTime(l1CacheA.expireTime()).limit(l1CacheA.limit()).timeUnit(l1CacheA.timeUnit())
                .cacheNullValue(l1CacheA.cacheNullValue()).keyGenerator(keyGenerator)
                .build();
    }

    protected L2CacheConfig buildCacheConfig(L2Cache l2CacheA, Method m) {
        String name = l2CacheA.name();
        if(!StringUtils.hasText(name)) {
            name = generateCacheName(m);
        }
        KeyGenerator keyGenerator = buildKeyGenerator(l2CacheA.keyGenerator());
        KeyConverter KeyConverter = buildKeyConverter(l2CacheA.keyConverter());
        RedissonClient redissonClient = buildRedissonClient(l2CacheA.clientBeanName());
        ObjectMapper objectMapper = buildObjectMapper();
        return L2CacheConfig.builder().name(name).refreshTime(l2CacheA.refreshTime())
                .expireTime(l2CacheA.expireTime()).timeUnit(l2CacheA.timeUnit()).cacheNullValue(l2CacheA.cacheNullValue())
                .keyGenerator(keyGenerator).keyConverter(KeyConverter).redissonClient(redissonClient).objectMapper(objectMapper)
                .build();
    }

}
