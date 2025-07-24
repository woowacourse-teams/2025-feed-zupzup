package feedzupzup.backend.feedback.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "좋아요 응답")
public record LikeResponse(
        @Schema(description = "좋아요 이전 횟수", example = "1")
        int beforeLikeCount,
        @Schema(description = "좋아요 이후 횟수", example = "2")
        int afterLikeCount
) {
}
