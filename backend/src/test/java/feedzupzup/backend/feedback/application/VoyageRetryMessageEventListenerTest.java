package feedzupzup.backend.feedback.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.github.sonus21.rqueue.core.RqueueMessageEnqueuer;
import feedzupzup.backend.feedback.application.dto.VoyageRetryTask;
import feedzupzup.backend.feedback.domain.VoyageRetryOutboxRepository;
import feedzupzup.backend.feedback.domain.event.VoyageRetryOutboxCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VoyageRetryMessageEventListenerTest {

    @Mock
    private RqueueMessageEnqueuer rqueueMessageEnqueuer;

    @Mock
    private VoyageRetryOutboxRepository outboxRepository;

    @InjectMocks
    private VoyageRetryMessageEventListener voyageRetryMessageEventListener;

    @Test
    @DisplayName("이벤트 처리 시 Redis 전송 및 Outbox 삭제")
    void handleOutboxCreated_sendsToRedisAndDeletesOutbox() {
        // given
        Long feedbackId = 1L;
        VoyageRetryOutboxCreatedEvent event = VoyageRetryOutboxCreatedEvent.of(feedbackId);

        // when
        voyageRetryMessageEventListener.handleOutboxCreated(event);

        // then
        // 1. Redis 전송 확인
        verify(rqueueMessageEnqueuer).enqueue(
                eq("voyage-retry-execution-queue"),
                any(VoyageRetryTask.class)
        );

        // 2. Outbox 삭제 확인
        verify(outboxRepository).deleteByFeedbackId(feedbackId);
    }

    @Test
    @DisplayName("Redis 전송 실패 시 예외 발생")
    void handleOutboxCreated_redisFailure_throwsException() {
        // given
        Long feedbackId = 1L;
        VoyageRetryOutboxCreatedEvent event = VoyageRetryOutboxCreatedEvent.of(feedbackId);

        // Redis 전송 실패 설정
        doThrow(new RuntimeException("Redis 연결 실패"))
                .when(rqueueMessageEnqueuer)
                .enqueue(any(), any());

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> voyageRetryMessageEventListener.handleOutboxCreated(event)
        );

        // Outbox 삭제가 호출되지 않아야 함 (트랜잭션 롤백)
        verify(outboxRepository, org.mockito.Mockito.never()).deleteByFeedbackId(any());
    }
}
