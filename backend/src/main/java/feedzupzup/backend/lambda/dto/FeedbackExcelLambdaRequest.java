package feedzupzup.backend.lambda.dto;

import java.util.List;

public record FeedbackExcelLambdaRequest(
        String jobId,
        String organizationUuid,
        List<FeedbackData> feedbacks
) {

    public record FeedbackData(
            Long id,
            String content,
            String category,
            String imageUrl,
            int likeCount,
            boolean isSecret,
            String status,
            String comment,
            String userName,
            String postedAt
    ) {

    }
}
