package feedzupzup.backend.global.config;

import static feedzupzup.backend.global.domain.CacheType.*;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
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
    public CacheManager cacheManager() {
        final SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(
                List.of(
                        createLatestFeedbacksCache(),
                        createLikesFeedbacksCache(),
                        createOldestFeedbacksCache()
                )
        );
        return cacheManager;
    }

    private CaffeineCache createLatestFeedbacksCache() {
        return new CaffeineCache(LATEST_FEEDBACK.getCacheName(),
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .recordStats()
                        .build()
        );
    }

    private CaffeineCache createLikesFeedbacksCache() {
        return new CaffeineCache(LIKES_FEEDBACK.getCacheName(),
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .recordStats()
                        .build()
        );
    }

    private CaffeineCache createOldestFeedbacksCache() {
        return new CaffeineCache(OLDEST_FEEDBACK.getCacheName(),
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .recordStats()
                        .build()
        );
    }

}
