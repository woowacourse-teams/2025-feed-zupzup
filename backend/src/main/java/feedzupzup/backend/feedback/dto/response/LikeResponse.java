package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.feedback.domain.Feedback;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "좋아요 응답")
public record LikeResponse(
        @Schema(description = "좋아요 횟수", example = "1")
        int afterLikeCount
) {

        public static LikeResponse from(final Feedback feedback) {
                return new LikeResponse(feedback.getLikeCount().getValue());
        }
}
