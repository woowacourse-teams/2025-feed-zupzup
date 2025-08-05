package feedzupzup.backend.category.domain;

import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Category {

    FACILITY("시설"),
    CURRICULUM("커리큘럼"),
    ETC("기타"),
    ;

    private final String koreanName;

    Category(final String koreanName) {
        this.koreanName = koreanName;
    }

    public boolean isSameCategory(String value) {
        return this.koreanName.equals(value);
    }

    public static Category findCategoryBy(String value) {
        return Arrays.stream(Category.values())
                .filter(result -> result.isSameCategory(value))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 카테고리입니다."));
    }
}
