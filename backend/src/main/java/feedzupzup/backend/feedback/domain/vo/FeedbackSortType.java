package feedzupzup.backend.feedback.domain.vo;


import feedzupzup.backend.global.domain.CacheType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FeedbackSortType {
    LATEST(CacheType.LATEST_FEEDBACK),
    OLDEST(CacheType.OLDEST_FEEDBACK),
    LIKES(CacheType.LIKES_FEEDBACK),
    ;

    private final CacheType cacheType;
}
