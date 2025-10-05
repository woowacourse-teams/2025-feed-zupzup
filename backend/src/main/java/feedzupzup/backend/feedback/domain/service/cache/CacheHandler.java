package feedzupzup.backend.feedback.domain.service.cache;


public interface CacheHandler<T, K> {

    void handle(T item, K key);
}
