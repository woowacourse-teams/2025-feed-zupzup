package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.feedback.domain.LikeFeedbacks;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Set;

@Schema(description = "좋아요 기록 조회")
public record LikeHistoryResponse(

        @Schema(description = "좋아요 횟수", example = "1")
        List<Long> likeHistories
)   {
        public static LikeHistoryResponse from(final LikeFeedbacks likeFeedbacks) {
            final Set<Long> likes = likeFeedbacks.getLikeFeedbacks();

            final List<Long> sortedLikeHistories = likes.stream()
                    .sorted()
                    .toList();
            return new LikeHistoryResponse(sortedLikeHistories);
        }
}
