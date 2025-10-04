package feedzupzup.backend.feedback.fixture;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.feedback.domain.vo.Content;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackClustering;
import feedzupzup.backend.feedback.domain.vo.LikeCount;
import feedzupzup.backend.feedback.domain.vo.PostedAt;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.domain.vo.UserName;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.global.util.CurrentDateTime;
import java.util.UUID;

public class FeedbackFixture {

    public static Feedback createFeedbackWithStatus(final Organization organization, final ProcessStatus status, final OrganizationCategory category) {
        return Feedback.builder()
                .content(new Content("상태별 피드백"))
                .isSecret(false)
                .status(status)
                .likeCount(new LikeCount(0))
                .organization(organization)
                .userName(new UserName("테스트유저"))
                .postedAt(new PostedAt(CurrentDateTime.create()))
                .organizationCategory(category)
                .build();
    }

    public static Feedback createFeedbackWithOrganization(final Organization organization, final OrganizationCategory category) {
        return Feedback.builder()
                .content(new Content("장소별 피드백"))
                .isSecret(false)
                .status(ProcessStatus.WAITING)
                .organization(organization)
                .likeCount(new LikeCount(0))
                .userName(new UserName("테스트유저"))
                .postedAt(new PostedAt(CurrentDateTime.create()))
                .organizationCategory(category)
                .build();
    }

    public static Feedback createFeedbackWithSecret(final Organization organization, final boolean isSecret, final OrganizationCategory category) {
        return Feedback.builder()
                .content(new Content("장소별 피드백"))
                .isSecret(isSecret)
                .status(ProcessStatus.WAITING)
                .organization(organization)
                .likeCount(new LikeCount(0))
                .userName(new UserName("테스트유저"))
                .postedAt(new PostedAt(CurrentDateTime.create()))
                .organizationCategory(category)
                .build();
    }

    public static Feedback createFeedbackWithContent(final Organization organization, final String content, final OrganizationCategory category) {
        return Feedback.builder()
                .content(new Content(content))
                .isSecret(false)
                .status(ProcessStatus.WAITING)
                .likeCount(new LikeCount(0))
                .organization(organization)
                .userName(new UserName("테스트유저"))
                .postedAt(new PostedAt(CurrentDateTime.create()))
                .organizationCategory(category)
                .build();
    }

    public static Feedback createFeedbackWithLikes(
            final Organization organization,
            final OrganizationCategory organizationCategory,
            final int likeCount
    ) {
        return Feedback.builder()
                .content(new Content("좋아요 테스트용 피드백"))
                .isSecret(false)
                .status(ProcessStatus.WAITING)
                .organization(organization)
                .likeCount(new LikeCount(likeCount))
                .userName(new UserName("테스트유저"))
                .postedAt(new PostedAt(CurrentDateTime.create()))
                .organizationCategory(organizationCategory)
                .build();
    }

    public static Feedback createFeedbackWithCluster(
            final Organization organization,
            final String content,
            final OrganizationCategory category,
            final UUID clusterId
    ) {
        FeedbackClustering clustering = new FeedbackClustering(
                clusterId,
                0.8, // similarity score
                new double[]{0.1, 0.2, 0.3} // embedding vector
        );
        
        return Feedback.builder()
                .content(new Content(content))
                .isSecret(false)
                .status(ProcessStatus.WAITING)
                .organization(organization)
                .likeCount(new LikeCount(0))
                .userName(new UserName("테스트유저"))
                .postedAt(new PostedAt(CurrentDateTime.create()))
                .organizationCategory(category)
                .clustering(clustering)
                .build();
    }

    public static Feedback createFeedbackWithCluster(
            final Organization organization,
            final String content,
            final OrganizationCategory category,
            final UUID clusterId,
            final double[] embeddingVector
    ) {
        FeedbackClustering clustering = new FeedbackClustering(
                clusterId,
                0.8, // similarity score
                embeddingVector
        );
        
        return Feedback.builder()
                .content(new Content(content))
                .isSecret(false)
                .status(ProcessStatus.WAITING)
                .organization(organization)
                .likeCount(new LikeCount(0))
                .userName(new UserName("테스트유저"))
                .postedAt(new PostedAt(CurrentDateTime.create()))
                .organizationCategory(category)
                .clustering(clustering)
                .build();
    }
}
