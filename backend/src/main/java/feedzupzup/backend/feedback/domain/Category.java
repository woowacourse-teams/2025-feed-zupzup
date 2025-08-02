package feedzupzup.backend.feedback.domain;

import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import java.util.Arrays;

public enum Category {

    FACILITY("시설"),
    ADMINISTRATION("행정"),
    CURRICULUM("커리큘럼"),
    ETC("기타"),
    ;

    private final String koreaName;

    Category(final String koreaName) {
        this.koreaName = koreaName;
    }

    public static Category from(String value) {
        return Arrays.stream(Category.values())
                .filter(category -> category.koreaName.equals(value))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 카테고리입니다."));
    }

    public String getName() {
        return koreaName;
    }
}
