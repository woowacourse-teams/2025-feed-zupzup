package feedzupzup.backend.s3.dto.request;

import jakarta.validation.constraints.NotNull;

public record PresignedUrlRequest(
        @NotNull(message = "피드백 ID는 필수입니다")
        Long feedbackId,
        @NotNull(message = "객체 타입은 필수입니다")
        String extension
) {

}
