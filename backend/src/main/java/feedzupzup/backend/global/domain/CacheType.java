package feedzupzup.backend.global.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CacheType {

    LATEST_FEEDBACK("latestFeedbacks", "latestCacheHandler"),
    OLDEST_FEEDBACK("oldestFeedbacks", "oldestCacheHandler"),
    LIKES_FEEDBACK("likesFeedbacks", "likesCacheHandler"),
    ;

    private final String cacheName;
    private final String handlerName;

}
