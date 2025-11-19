package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.feedback.domain.vo.FeedbackDownloadJob;
import feedzupzup.backend.feedback.domain.vo.FeedbackDownloadJob.DownloadStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "피드백 다운로드 작업 상태 응답")
public record FeedbackDownloadJobStatusResponse(
        @Schema(description = "작업 상태", example = "PROCESSING")
        DownloadStatus jobStatus,

        @Schema(description = "진행률 (0-100)", example = "30")
        int progress
) {

    public static FeedbackDownloadJobStatusResponse from(final FeedbackDownloadJob job) {
        return new FeedbackDownloadJobStatusResponse(
                job.getStatus(),
                job.getProgress()
        );
    }
}
