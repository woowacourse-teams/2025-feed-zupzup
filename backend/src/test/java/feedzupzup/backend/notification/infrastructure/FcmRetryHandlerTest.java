package feedzupzup.backend.notification.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import feedzupzup.backend.notification.exception.NotificationException.RetryInterruptedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FcmRetryHandlerTest {

    private final FcmRetryHandler fcmRetryHandler = new FcmRetryHandler();

    @Nested
    @DisplayName("재시도 가능 여부 판단")
    class ShouldRetry {

        @Test
        @DisplayName("UNAVAILABLE 에러 코드는 재시도한다")
        void shouldRetry_UnavailableError_ReturnsTrue() {
            // given
            FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
            given(exception.getMessagingErrorCode()).willReturn(MessagingErrorCode.UNAVAILABLE);

            // when
            boolean result = fcmRetryHandler.shouldRetry(exception, 0);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("INTERNAL 에러 코드는 재시도한다")
        void shouldRetry_InternalError_ReturnsTrue() {
            // given
            FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
            given(exception.getMessagingErrorCode()).willReturn(MessagingErrorCode.INTERNAL);

            // when
            boolean result = fcmRetryHandler.shouldRetry(exception, 0);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("QUOTA_EXCEEDED 에러 코드는 재시도한다")
        void shouldRetry_QuotaExceededError_ReturnsTrue() {
            // given
            FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
            given(exception.getMessagingErrorCode()).willReturn(MessagingErrorCode.QUOTA_EXCEEDED);

            // when
            boolean result = fcmRetryHandler.shouldRetry(exception, 0);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("INVALID_ARGUMENT 에러 코드는 재시도하지 않는다")
        void shouldRetry_InvalidArgumentError_ReturnsFalse() {
            // given
            FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
            given(exception.getMessagingErrorCode()).willReturn(MessagingErrorCode.INVALID_ARGUMENT);

            // when
            boolean result = fcmRetryHandler.shouldRetry(exception, 0);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("SENDER_ID_MISMATCH 에러 코드는 재시도하지 않는다")
        void shouldRetry_SenderIdMismatchError_ReturnsFalse() {
            // given
            FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
            given(exception.getMessagingErrorCode()).willReturn(MessagingErrorCode.SENDER_ID_MISMATCH);

            // when
            boolean result = fcmRetryHandler.shouldRetry(exception, 0);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("UNREGISTERED 에러 코드는 재시도하지 않는다")
        void shouldRetry_UnregisteredError_ReturnsFalse() {
            // given
            FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
            given(exception.getMessagingErrorCode()).willReturn(MessagingErrorCode.UNREGISTERED);

            // when
            boolean result = fcmRetryHandler.shouldRetry(exception, 0);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("최대 재시도 횟수에 도달하면 재시도하지 않는다")
        void shouldRetry_MaxAttemptsReached_ReturnsFalse() {
            // given
            FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
            given(exception.getMessagingErrorCode()).willReturn(MessagingErrorCode.UNAVAILABLE);

            // when
            boolean result = fcmRetryHandler.shouldRetry(exception, 4);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("FirebaseMessagingException이 아닌 예외는 재시도하지 않는다")
        void shouldRetry_NotFirebaseMessagingException_ReturnsFalse() {
            // given
            RuntimeException exception = new RuntimeException("General error");

            // when
            boolean result = fcmRetryHandler.shouldRetry(exception, 0);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("재시도 대기")
    class WaitBeforeRetry {

        @Test
        @DisplayName("정상적으로 대기 시간을 계산한다")
        void waitBeforeRetry_CalculatesDelay() {
            // when & then - 예외가 발생하지 않으면 성공
            fcmRetryHandler.waitBeforeRetry(0);
        }

        @Test
        @DisplayName("스레드 인터럽트 발생 시 RetryInterruptedException을 던진다")
        void waitBeforeRetry_InterruptedException_ThrowsRetryInterruptedException() {
            // given
            Thread.currentThread().interrupt();

            // when & then
            assertThatThrownBy(() -> fcmRetryHandler.waitBeforeRetry(0))
                    .isInstanceOf(RetryInterruptedException.class)
                    .hasMessageContaining("재시도 대기 중 인터럽트 발생");

            // 인터럽트 상태 복원
            Thread.interrupted();
        }
    }
}