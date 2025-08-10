package feedzupzup.backend.feedback.fixture;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.feedback.domain.vo.Content;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.vo.PostedAt;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.domain.vo.UserName;

public class FeedbackFixture {

    public static Feedback createFeedbackWithStatus(final ProcessStatus status, final OrganizationCategory category) {
        return Feedback.builder()
                .content(new Content("상태별 피드백"))
                .isSecret(false)
                .status(status)
                .organizationId(1L)
                .userName(new UserName("테스트유저"))
                .postedAt(PostedAt.createTimeInSeoul())
                .organizationCategory(category)
                .build();
    }

    public static Feedback createFeedbackWithOrganizationId(final Long organizationId, final OrganizationCategory category) {
        return Feedback.builder()
                .content(new Content("장소별 피드백"))
                .isSecret(false)
                .status(ProcessStatus.WAITING)
                .organizationId(organizationId)
                .userName(new UserName("테스트유저"))
                .postedAt(PostedAt.createTimeInSeoul())
                .organizationCategory(category)
                .build();
    }

    public static Feedback createFeedbackWithSecret(final boolean isSecret, final OrganizationCategory category) {
        return Feedback.builder()
                .content(new Content("장소별 피드백"))
                .isSecret(isSecret)
                .status(ProcessStatus.WAITING)
                .organizationId(1L)
                .userName(new UserName("테스트유저"))
                .postedAt(PostedAt.createTimeInSeoul())
                .organizationCategory(category)
                .build();
    }

    public static Feedback createFeedbackWithContent(final String content, final OrganizationCategory category) {
        return Feedback.builder()
                .content(new Content(content))
                .isSecret(false)
                .status(ProcessStatus.WAITING)
                .organizationId(1L)
                .userName(new UserName("테스트유저"))
                .postedAt(PostedAt.createTimeInSeoul())
                .organizationCategory(category)
                .build();
    }

    public static Feedback createFeedbackWithLikes(
            final Long organizationId,
            final OrganizationCategory organizationCategory,
            final int likeCount
    ) {
        return Feedback.builder()
                .content(new Content("좋아요 테스트용 피드백"))
                .isSecret(false)
                .status(ProcessStatus.WAITING)
                .organizationId(organizationId)
                .likeCount(likeCount)
                .userName(new UserName("테스트유저"))
                .postedAt(PostedAt.createTimeInSeoul())
                .organizationCategory(organizationCategory)
                .build();
    }
}
