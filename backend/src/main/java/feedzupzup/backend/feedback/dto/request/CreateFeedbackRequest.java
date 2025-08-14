package feedzupzup.backend.feedback.dto.request;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.feedback.domain.vo.Content;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.vo.PostedAt;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.domain.vo.UserName;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.util.CurrentDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Length;

@Schema(description = "피드백 생성 요청")
public record CreateFeedbackRequest(
        @Schema(description = "피드백 내용", example = "급식실 음식 간이 너무 짜요")
        String content,

        @Schema(description = "비밀 피드백 여부", example = "false")
        boolean isSecret,

        @Schema(description = "작성자 이름", example = "댕댕이")
        @Length(min = 1, max = 10, message = "작성자 이름 1글자 이상 10글자 이하여야 합니다.")
        String userName,

        @Schema(description = "카테고리", example = "시설")
        String category
) {

    public Feedback toFeedback(final Organization organization, final OrganizationCategory organizationCategory) {
        return Feedback.builder()
                .content(new Content(content))
                .organization(organization)
                .status(ProcessStatus.WAITING)
                .isSecret(isSecret)
                .userName(new UserName(userName))
                .postedAt(new PostedAt(CurrentDateTime.create()))
                .organizationCategory(organizationCategory)
                .build();
    }
}
