package feedzupzup.backend.feedback.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "좋아요 응답")
public record LikeResponse(
        @Schema(description = "좋아요 상태", example = "true")
        boolean isLiked
) {

}
