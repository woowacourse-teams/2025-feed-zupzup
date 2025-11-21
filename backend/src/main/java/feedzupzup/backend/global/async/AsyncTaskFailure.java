package feedzupzup.backend.global.async;

import feedzupzup.backend.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "async_task_failure")
public class AsyncTaskFailure extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false, length = 50)
    private TaskType taskType;

    @Column(name = "target_type", nullable = false, length = 20)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false, length = 100)
    private String targetId;

    @Column(name = "error_message", nullable = false, columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "is_retryable", nullable = false)
    private boolean retryable;

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FailureStatus status;

    @Builder
    private AsyncTaskFailure(
            final TaskType taskType,
            final TargetType targetType,
            final String targetId,
            final String errorMessage,
            final boolean retryable,
            final FailureStatus status
    ) {
        this.taskType = taskType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.errorMessage = errorMessage;
        this.retryable = retryable;
        this.status = status;
    }

    public static AsyncTaskFailure create(
            final TaskType taskType,
            final TargetType targetType,
            final String targetId,
            final String errorMessage,
            final boolean retryable
    ) {
        if (retryable) {
            return new AsyncTaskFailure(taskType, targetType, targetId, errorMessage, true, FailureStatus.PENDING);
        }
        return new AsyncTaskFailure(taskType, targetType, targetId, errorMessage, false, FailureStatus.FINAL_FAILED);
    }

    public void incrementRetryCount() {
        if (this.retryCount >= 3) {
            finalFailed();
            throw new IllegalStateException("최대 재시도 횟수는 3회입니다. 그 이상 시도할 수 없습니다.");
        }
        this.retryCount++;
        this.status = FailureStatus.RETRYING;
    }

    public boolean isFinalFailed() {
        return this.status == FailureStatus.FINAL_FAILED;
    }

    public void finalFailed() {
        this.retryable = false;
        this.status = FailureStatus.FINAL_FAILED;
    }

    public boolean isMaxRetryCount() {
        return this.retryCount == 3;
    }
}
