package feedzupzup.backend.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .recordStats();
    }

    @Bean
    public CacheManager cacheManager(final Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(
                "latestFeedbacks",
                "likesFeedbacks",
                "oldestFeedbacks"
        );
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

}
