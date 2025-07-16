package feedzupzup.backend.feedback.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "좋아요 응답")
public record UnLikeResponse(
        @Schema(description = "좋아요 상태", example = "false")
        boolean isLiked
) {

}
