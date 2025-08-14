package feedzupzup.backend.feedback.domain.vo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FeedbackSortBy {
    LATEST(),
    OLDEST,
    LIKES;
}
