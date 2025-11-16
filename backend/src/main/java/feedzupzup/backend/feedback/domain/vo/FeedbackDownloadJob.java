package feedzupzup.backend.feedback.domain.vo;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedbackDownloadJob {

    enum DownloadStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }

    private String jobId;
    private String organizationUuid;
    private DownloadStatus status;
    private int progress;
    private String downloadUrl;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FeedbackDownloadJob create(final String organizationUuid) {
        final String jobId = UUID.randomUUID().toString();
        final LocalDateTime now = LocalDateTime.now();
        
        return new FeedbackDownloadJob(
                jobId,
                organizationUuid,
                DownloadStatus.PENDING,
                0,
                null,
                null,
                now,
                now
        );
    }

    public void updateProgress(final int newProgress) {
        this.progress = newProgress;
        this.status = DownloadStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }

    public void completeWithUrl(final String downloadUrl) {
        this.status = DownloadStatus.COMPLETED;
        this.progress = 100;
        this.downloadUrl = downloadUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void fail(final String errorMessage) {
        this.status = DownloadStatus.FAILED;
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }
}
