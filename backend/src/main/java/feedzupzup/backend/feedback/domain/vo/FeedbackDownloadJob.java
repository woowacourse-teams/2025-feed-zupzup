package feedzupzup.backend.feedback.domain.vo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DynamoDbBean
public class FeedbackDownloadJob {

    public enum DownloadStatus {
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
    private Long createdAtEpochMilli;
    private Long updatedAtEpochMilli;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("jobId")
    public String getJobId() {
        return jobId;
    }

    @DynamoDbAttribute("organizationUuid")
    public String getOrganizationUuid() {
        return organizationUuid;
    }

    @DynamoDbAttribute("status")
    public String getStatusString() {
        return status != null ? status.name() : null;
    }

    public void setStatusString(final String statusString) {
        this.status = statusString != null ? DownloadStatus.valueOf(statusString) : null;
    }

    @DynamoDbAttribute("progress")
    public int getProgress() {
        return progress;
    }

    @DynamoDbAttribute("downloadUrl")
    public String getDownloadUrl() {
        return downloadUrl;
    }

    @DynamoDbAttribute("errorMessage")
    public String getErrorMessage() {
        return errorMessage;
    }

    @DynamoDbAttribute("createdAtEpochMilli")
    public Long getCreatedAtEpochMilli() {
        return createdAtEpochMilli;
    }

    @DynamoDbAttribute("updatedAtEpochMilli")
    public Long getUpdatedAtEpochMilli() {
        return updatedAtEpochMilli;
    }

    public LocalDateTime getCreatedAt() {
        return createdAtEpochMilli != null
                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAtEpochMilli), ZoneId.systemDefault())
                : null;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAtEpochMilli = createdAt != null
                ? createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                : null;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAtEpochMilli != null
                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(updatedAtEpochMilli), ZoneId.systemDefault())
                : null;
    }

    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAtEpochMilli = updatedAt != null
                ? updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                : null;
    }

    public static FeedbackDownloadJob create(final String organizationUuid) {
        final String jobId = UUID.randomUUID().toString();
        final long nowEpochMilli = System.currentTimeMillis();

        return new FeedbackDownloadJob(
                jobId,
                organizationUuid,
                DownloadStatus.PENDING,
                0,
                null,
                null,
                nowEpochMilli,
                nowEpochMilli
        );
    }

    public void updateProgress(final int newProgress) {
        this.progress = newProgress;
        this.status = DownloadStatus.PROCESSING;
        this.updatedAtEpochMilli = System.currentTimeMillis();
    }

    public void completeWithUrl(final String downloadUrl) {
        this.status = DownloadStatus.COMPLETED;
        this.progress = 100;
        this.downloadUrl = downloadUrl;
        this.updatedAtEpochMilli = System.currentTimeMillis();
    }

    public void fail(final String errorMessage) {
        this.status = DownloadStatus.FAILED;
        this.errorMessage = errorMessage;
        this.updatedAtEpochMilli = System.currentTimeMillis();
    }
}
