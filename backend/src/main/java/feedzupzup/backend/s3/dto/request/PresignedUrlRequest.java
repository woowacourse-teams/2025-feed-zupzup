package feedzupzup.backend.s3.dto.request;

import jakarta.validation.constraints.NotNull;

public record PresignedUrlRequest(
        @NotNull(message = "객체 ID는 필수입니다")
        Long objectId,
        @NotNull(message = "객체 타입은 필수입니다")
        String extension,
        @NotNull(message = "객체 디렉토리는 필수입니다")
        String objectDir
) {

}
