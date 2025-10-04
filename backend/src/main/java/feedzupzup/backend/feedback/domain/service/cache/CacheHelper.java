package feedzupzup.backend.feedback.domain.service.cache;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class CacheHelper {

    private final CacheManager cacheManager;

    /**
     * 캐시에서 값을 가져오는 제네릭 메서드.
     * @param key 캐시 키 ex) UUID OrganizationUuid
     * @param cacheName 캐시 이름 ex) latestFeedbacksCache, likesFeedbackCache
     * @param valueType 반환받고 싶은 값의 클래스 타입 ex) String.class, List.class)
     */

    public <T> Optional<T> getCacheValue(final Object key, final String cacheName, final Class<T> valueType) {
        return Optional.ofNullable(cacheManager.getCache(cacheName))
                .map(cache -> cache.get(key, valueType));
    }
    public <T> Optional<List<T>> getCacheValueList(final Object key, final String cacheName) {
        return this.getCacheValue(key, cacheName, List.class)
                .map(list -> (List<T>) list);
    }

    public void removeCache(final Object key, final String cacheName) {
        Optional.ofNullable(cacheManager.getCache(cacheName))
                .ifPresent(cache -> cache.evict(key));
    }

    public <K, V> void putInCache(final String cacheName, final K key, final V value) {
        Optional.ofNullable(cacheManager.getCache(cacheName))
                .ifPresent(cache -> cache.put(key, value));
    }
}
