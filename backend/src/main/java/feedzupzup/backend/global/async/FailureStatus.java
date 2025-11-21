package feedzupzup.backend.global.async;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FailureStatus {
    PENDING("대기 중"),
    RETRYING("재시도 중"),
    FINAL_FAILED("최종 실패")
    ;

    private final String description;
}
