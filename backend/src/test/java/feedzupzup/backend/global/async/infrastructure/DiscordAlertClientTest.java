package feedzupzup.backend.global.async.infrastructure;

import static feedzupzup.backend.global.async.TargetType.FEEDBACK;
import static feedzupzup.backend.global.async.TaskType.CLUSTER_LABEL_GENERATION;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.global.async.AsyncFailureAlertService;
import feedzupzup.backend.global.async.AsyncTaskFailure;
import feedzupzup.backend.global.async.AsyncTaskFailureRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@Disabled
class DiscordAlertClientTest extends ServiceIntegrationHelper {

    @Autowired
    private AsyncFailureAlertService asyncFailureAlertService;

    @Autowired
    private AsyncTaskFailureRepository asyncTaskFailureRepository;

    @Test
    @DisplayName("재시도 불가능한 실패에 대한 Discord 알람이 정상적으로 전송된다")
    void when_non_retryable_failure_then_discord_alert_sent_successfully() {
        // given
        AsyncTaskFailure failure = AsyncTaskFailure.create(
                CLUSTER_LABEL_GENERATION,
                FEEDBACK,
                "67890",
                "OpenAI API 인증 실패: 잘못된 API 키가 제공되었습니다.",
                false
        );
        AsyncTaskFailure savedFailure = asyncTaskFailureRepository.save(failure);

        // when & then - 실제 Discord 전송은 외부 의존성이므로 예외가 발생하지 않음을 확인
        assertDoesNotThrow(() -> {
            asyncFailureAlertService.alert(savedFailure.getId());
        });
    }
}
