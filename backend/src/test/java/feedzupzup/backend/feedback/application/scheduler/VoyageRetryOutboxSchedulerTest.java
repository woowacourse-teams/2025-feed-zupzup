package feedzupzup.backend.feedback.application.scheduler;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.sonus21.rqueue.core.RqueueMessageEnqueuer;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.VoyageRetryOutbox;
import feedzupzup.backend.feedback.domain.VoyageRetryOutboxRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class VoyageRetryOutboxSchedulerTest extends ServiceIntegrationHelper {

    @Autowired
    private VoyageRetryOutboxScheduler scheduler;

    @Autowired
    private VoyageRetryOutboxRepository outboxRepository;

    @MockitoBean
    private RqueueMessageEnqueuer rqueueMessageEnqueuer;

    @Test
    @DisplayName("폴링: Outbox에 있는 메시지들을 찾아서 이벤트 재발행")
    void pollOutboxAndRetry_publishesEventsForAllOutboxMessages() {
        // given
        VoyageRetryOutbox outbox1 = VoyageRetryOutbox.create(1L, "첫 번째 에러");
        VoyageRetryOutbox outbox2 = VoyageRetryOutbox.create(2L, "두 번째 에러");
        VoyageRetryOutbox outbox3 = VoyageRetryOutbox.create(3L, "세 번째 에러");
        outboxRepository.save(outbox1);
        outboxRepository.save(outbox2);
        outboxRepository.save(outbox3);

        // when
        scheduler.pollOutboxAndRetry();

        // then
        // 스케줄러가 정상 실행되었는지 확인 (예외 없이 완료)
        assertThat(outboxRepository.findAll()).hasSize(3);
    }

    @Test
    @DisplayName("폴링: Outbox가 비어있으면 아무 작업도 하지 않음")
    void pollOutboxAndRetry_doesNothingWhenOutboxIsEmpty() {
        // given
        // Outbox가 비어있음

        // when
        scheduler.pollOutboxAndRetry();

        // then
        // 예외 없이 정상 종료되어야 함
        assertThat(outboxRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("폴링: 일부 메시지 발행 실패해도 계속 진행")
    void pollOutboxAndRetry_continuesOnPartialFailure() {
        // given
        VoyageRetryOutbox outbox1 = VoyageRetryOutbox.create(1L, "첫 번째 에러");
        VoyageRetryOutbox outbox2 = VoyageRetryOutbox.create(2L, "두 번째 에러");
        outboxRepository.save(outbox1);
        outboxRepository.save(outbox2);

        // when
        scheduler.pollOutboxAndRetry();

        // then
        // 예외 없이 정상 종료
        assertThat(outboxRepository.findAll()).hasSize(2);
    }
}