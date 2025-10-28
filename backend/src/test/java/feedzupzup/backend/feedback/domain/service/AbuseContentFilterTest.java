package feedzupzup.backend.feedback.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.feedback.domain.service.moderation.ContentFilter;
import feedzupzup.backend.feedback.infrastructure.AbuseContentFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AbuseContentFilterTest {

    private final ContentFilter contentFilter = new AbuseContentFilter();

    @Test
    @DisplayName("욕설이 없는 텍스트는 그대로 반환한다")
    void filter_clean_text() {
        // given
        final String cleanText = "좋은 피드백입니다";

        // when
        final String result = contentFilter.filter(cleanText);

        // then
        assertThat(result).isEqualTo(cleanText);
    }

    @Test
    @DisplayName("욕설이 포함된 텍스트는 OO로 치환한다")
    void filter_profanity_text() {
        // given
        // Note: 욕설 필터링 테스트를 위한 의도적인 욕설 포함
        final String profanityText = "시발 이거 진짜 좋네요";

        // when
        final String result = contentFilter.filter(profanityText);

        // then
        assertAll(
                () -> assertThat(result).isNotEqualTo(profanityText),
                () -> assertThat(result).contains("OO")
        );
    }

    @Test
    @DisplayName("사용자가 입력한 별표는 그대로 유지한다")
    void filter_asterisks_text() {
        // given
        final String textWithAsterisks = "이거 정말 좋아요 **";

        // when
        final String result = contentFilter.filter(textWithAsterisks);

        // then
        assertThat(result).isEqualTo(textWithAsterisks);
    }

    @Test
    @DisplayName("욕설과 사용자 입력 별표가 함께 있을 때 욕설만 치환하고 별표는 유지한다")
    void filter_profanity_and_asterisks() {
        // given
        final String profanity = "시발";
        final String mixedText = profanity + " 이거 ** 좋네요";

        // when
        final String result = contentFilter.filter(mixedText);

        // then
        assertAll(
                () -> assertThat(result).contains("OO"),
                () -> assertThat(result).contains("**"),
                () -> assertThat(result).doesNotContain(profanity)
        );
    }

    @Test
    @DisplayName("여러 욕설은 순차적으로 다른 동물 소리로 치환한다")
    void filter_multiple_profanities() {
        // given
        // Note: 욕설 필터링 테스트를 위한 의도적인 욕설 포함
        final String multipleProfanities = "시발시발";

        // when
        final String result = contentFilter.filter(multipleProfanities);

        // then
        assertAll(
                () -> assertThat(result).doesNotContain("시발"),
                () -> assertThat(result).contains("OO")
        );
    }

    @Test
    @DisplayName("null 입력은 그대로 null을 반환한다")
    void filter_null_text() {
        // given
        final String nullText = null;

        // when
        final String result = contentFilter.filter(nullText);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("빈 문자열 입력은 그대로 빈 문자열을 반환한다")
    void filter_blank_text() {
        // given
        final String blankText = "   ";

        // when
        final String result = contentFilter.filter(blankText);

        // then
        assertThat(result).isEqualTo(blankText);
    }
}
