package feedzupzup.backend.feedback.domain;

import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum StatisticTargetPeriod {

    TODAY(0),
    WEEK(6),
    ;

    private final int value;

    StatisticTargetPeriod(final int value) {
        this.value = value;
    }

    public static StatisticTargetPeriod from(String givenPeriod) {
        return Arrays.stream(StatisticTargetPeriod.values())
                .filter(period -> period.name().equals(givenPeriod))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("해당 값은 제공되지 않습니다."));
    }
}
