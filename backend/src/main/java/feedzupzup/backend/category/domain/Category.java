package feedzupzup.backend.category.domain;

import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {

    REPORT("신고"),
    QUESTION("질문"),
    SUGGESTION("건의"),
    FEEDBACK("피드백"),
    COMPLIMENT("칭찬"),
    SHARING("정보공유"),
    ETC("기타"),
    ;

    private final String koreanName;

    public boolean isSameCategory(final String value) {
        return this.koreanName.equals(value);
    }

    public static Category findCategoryBy(final String value) {
        return Arrays.stream(Category.values())
                .filter(result -> result.isSameCategory(value))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 카테고리입니다."));
    }

    public static boolean hasCategory(final String value) {
        return Arrays.stream(Category.values())
                .anyMatch(category -> category.isSameCategory(value));
    }
}
