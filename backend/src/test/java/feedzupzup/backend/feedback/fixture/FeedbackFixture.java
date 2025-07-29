package feedzupzup.backend.feedback.fixture;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.ProcessStatus;
import feedzupzup.backend.feedback.domain.UserName;

public class FeedbackFixture {


    public static Feedback createFeedbackWithStatus(final ProcessStatus status) {
        return Feedback.builder()
                .content("상태별 피드백")
                .isSecret(false)
                .status(status)
                .organizationId(1L)
                .userName(new UserName("테스트유저"))
                .build();
    }

    public static Feedback createFeedbackWithOrganizationId(final Long organizationId) {
        return Feedback.builder()
                .content("장소별 피드백")
                .isSecret(false)
                .status(ProcessStatus.WAITING)
                .organizationId(organizationId)
                .userName(new UserName("테스트유저"))
                .build();
    }

    public static Feedback createFeedbackWithSecret(final boolean isSecret) {
        return Feedback.builder()
                .content("장소별 피드백")
                .isSecret(isSecret)
                .status(ProcessStatus.WAITING)
                .organizationId(1L)
                .userName(new UserName("테스트유저"))
                .build();
    }

    public static Feedback createFeedbackWithContent(final String content) {
        return Feedback.builder()
                .content(content)
                .isSecret(false)
                .status(ProcessStatus.WAITING)
                .organizationId(1L)
                .userName(new UserName("테스트유저"))
                .build();
    }

    public static Feedback createFeedbackWithLikes(final Long organizationId, final int likeCount) {
        return Feedback.builder()
                .content("좋아요 테스트용 피드백")
                .isSecret(false)
                .status(ProcessStatus.WAITING)
                .organizationId(organizationId)
                .likeCount(likeCount)
                .userName(new UserName("테스트유저"))
                .build();
    }
}
