package fintech.presence;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
public class PresenceConfiguration {

    @Bean
    @Primary
    public CacheManager presenceCacheManagerToken() {
        GuavaCacheManager cacheManager = new GuavaCacheManager();
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(20, TimeUnit.MINUTES);
        cacheManager.setCacheBuilder(cacheBuilder);
        return cacheManager;
    }

    @Bean
    public CacheManager presenceCacheManager30Mins() {
        GuavaCacheManager cacheManager = new GuavaCacheManager();
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(30, TimeUnit.MINUTES);
        cacheManager.setCacheBuilder(cacheBuilder);
        return cacheManager;
    }

    @Bean
    public CacheManager presenceCacheManager12Hours() {
        GuavaCacheManager cacheManager = new GuavaCacheManager();
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(12, TimeUnit.HOURS);
        cacheManager.setCacheBuilder(cacheBuilder);
        return cacheManager;
    }
}
