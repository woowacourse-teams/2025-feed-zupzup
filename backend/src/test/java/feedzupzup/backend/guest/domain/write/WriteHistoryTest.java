package feedzupzup.backend.guest.domain.write;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.guest.domain.like.LikeHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WriteHistoryTest {

    @Mock
    private Feedback feedback;

    @Mock
    private Guest guest;

    @DisplayName("피드백 히스토리에 게스트가 Null 이면 예외가 발생한다")
    @Test
    void likeHistory_guest_null_exception() {
        // when & then
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new LikeHistory(null, feedback));
    }

    @DisplayName("피드백 히스토리에 피드백이 Null이면 예외가 발생한다")
    @Test
    void like_history_null_exception() {
        // when & then
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new LikeHistory(guest, null));
    }

}
