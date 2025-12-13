package feedzupzup.backend.feedback.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.sonus21.rqueue.core.RqueueMessageEnqueuer;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.VoyageRetryOutbox;
import feedzupzup.backend.feedback.domain.VoyageRetryOutboxRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class VoyageRetryQueueServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private VoyageRetryQueueService voyageRetryQueueService;

    @Autowired
    private VoyageRetryOutboxRepository outboxRepository;

    @MockitoBean
    private RqueueMessageEnqueuer rqueueMessageEnqueuer;

    @Test
    @DisplayName("재시도 스케줄링 시 Outbox 저장")
    void retryTask() {
        // given
        Long feedbackId = 1L;
        String errorMessage = "Voyage AI 호출 실패";

        // when
        voyageRetryQueueService.retryTask(feedbackId, errorMessage);

        // then
        VoyageRetryOutbox savedOutbox = outboxRepository.findByFeedbackId(feedbackId)
                .orElseThrow();
        assertThat(savedOutbox.getFeedbackId()).isEqualTo(feedbackId);
        assertThat(savedOutbox.getErrorMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("같은 feedbackId로 중복 Outbox 생성 시 예외 발생")
    void retryTask_duplicateFeedbackId_throwsException() {
        // given
        Long feedbackId = 1L;
        voyageRetryQueueService.retryTask(feedbackId, "첫 번째 에러");

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> voyageRetryQueueService.retryTask(feedbackId, "두 번째 에러")
        );
    }

    @Test
    @DisplayName("Outbox 삭제")
    void deleteOutboxByFeedbackId_deletesOutbox() {
        // given
        Long feedbackId = 1L;
        VoyageRetryOutbox outbox = VoyageRetryOutbox.create(feedbackId, "에러 메시지");
        outboxRepository.save(outbox);

        // when
        voyageRetryQueueService.deleteOutboxByFeedbackId(feedbackId);

        // then
        assertThat(outboxRepository.findByFeedbackId(feedbackId)).isEmpty();
    }
}
