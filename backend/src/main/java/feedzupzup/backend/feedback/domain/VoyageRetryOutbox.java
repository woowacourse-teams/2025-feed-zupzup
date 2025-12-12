package feedzupzup.backend.feedback.domain;

import feedzupzup.backend.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Voyage AI 재시도 Outbox
 *
 * 목적: 클러스터링 실패 시 메시지 유실 방지 (At-least-once delivery)
 * 수명: Redis로 메시지 전송 후 즉시 삭제
 * 재시도 관리: 100% Redis(Rqueue)가 담당
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "voyage_retry_outbox")
public class VoyageRetryOutbox extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "feedback_id", nullable = false, unique = true)
    private Long feedbackId;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Builder
    private VoyageRetryOutbox(
            final Long feedbackId,
            final String errorMessage
    ) {
        this.feedbackId = feedbackId;
        this.errorMessage = errorMessage;
    }

    public static VoyageRetryOutbox create(final Long feedbackId, final String errorMessage) {
        return VoyageRetryOutbox.builder()
                .feedbackId(feedbackId)
                .errorMessage(errorMessage)
                .build();
    }
}