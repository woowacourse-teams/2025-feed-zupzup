package feedzupzup.backend.feedback.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTest {

    @Test
    @DisplayName("정상적인 내용으로 Content를 생성한다")
    void createContent_withValidContent() {
        // given
        final String validContent = "좋은 피드백입니다";

        // when
        final Content content = new Content(validContent);

        // then
        assertThat(content.getValue()).isEqualTo(validContent);
    }

    @Test
    @DisplayName("욕설이 포함된 내용은 동물 소리로 치환된다")
    void createContent_withProfanity_replacesWithAnimalSounds() {
        // given
        final String profanityContent = "시발 이거 진짜 좋네요";

        // when
        final Content content = new Content(profanityContent);

        // then
        assertThat(content.getValue()).isNotEqualTo(profanityContent);
        assertThat(content.getValue()).containsAnyOf("멍멍", "야옹", "음메");
    }

    @Test
    @DisplayName("사용자가 입력한 별표는 그대로 유지된다")
    void createContent_withAsterisks_keepsOriginal() {
        // given
        final String contentWithAsterisks = "이거 정말 좋아요 **";

        // when
        final Content content = new Content(contentWithAsterisks);

        // then
        assertThat(content.getValue()).isEqualTo(contentWithAsterisks);
    }
}
