package feedzupzup.backend.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
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
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(
                Arrays.asList(
                        createLatestFeedbacksCache(),
                        createLikesFeedbacksCache(),
                        createOldestFeedbacksCache()
                )
        );
        return cacheManager;
    }

    private CaffeineCache createLatestFeedbacksCache() {
        return new CaffeineCache("latestFeedbacks",
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .recordStats()
                        .build()
        );
    }

    private CaffeineCache createLikesFeedbacksCache() {
        return new CaffeineCache("likesFeedbacks",
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .recordStats()
                        .build()
        );
    }

    private CaffeineCache createOldestFeedbacksCache() {
        return new CaffeineCache("oldestFeedbacks",
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(30, TimeUnit.SECONDS) // 30초
                        .recordStats()
                        .build()
        );
    }

}
