package feedzupzup.backend.feedback.dto.request;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.ImageUrl;
import feedzupzup.backend.feedback.domain.ProcessStatus;
import feedzupzup.backend.feedback.domain.UserName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Length;

@Schema(description = "피드백 생성 요청")
public record CreateFeedbackRequest(
        @Schema(description = "피드백 내용", example = "급식실 음식 간이 너무 짜요")
        String content,

        @Schema(description = "이미지 URL", example = "https://bucket.s3.amazonaws.com/posts/uuid-image.jpg")
        String imageUrl,

        @Schema(description = "비밀 피드백 여부", example = "false")
        boolean isSecret,

        @Schema(description = "작성자 이름", example = "댕댕이")
        @Length(min = 1, max = 10, message = "작성자 이름 1글자 이상 10글자 이하여야 합니다.")
        String userName
) {

    public Feedback toFeedback(final Long groupId, final ImageUrl imageUrl) {
        return Feedback.builder()
                .content(content)
                .imageUrl(imageUrl)
                .groupId(groupId)
                .status(ProcessStatus.WAITING)
                .isSecret(isSecret)
                .userName(new UserName(userName))
                .build();
    }
}
