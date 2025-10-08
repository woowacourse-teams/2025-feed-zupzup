package feedzupzup.backend.guest.dto.response;

import feedzupzup.backend.guest.domain.like.LikeHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "좋아요 기록 조회")
public record LikeHistoryResponse(

        @Schema(description = "좋아요 누른 피드백 ID 목록(오름차순)", example = "[1, 3, 5]")
        List<Long> feedbackIds
)   {
        public static LikeHistoryResponse from(final List<LikeHistory> likeHistories) {
            return new LikeHistoryResponse(likeHistories.stream()
                    .map(likehistory -> likehistory.getFeedback().getId())
                    .sorted()
                    .toList()
            );
        }
}
