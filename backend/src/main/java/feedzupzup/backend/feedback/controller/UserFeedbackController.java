package feedzupzup.backend.feedback.controller;

import feedzupzup.backend.feedback.api.UserFeedbackApi;
import feedzupzup.backend.feedback.application.FeedbackLikeService;
import feedzupzup.backend.feedback.application.FeedbackStatisticService;
import feedzupzup.backend.feedback.application.UserFeedbackService;
import feedzupzup.backend.feedback.domain.vo.FeedbackOrderBy;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import feedzupzup.backend.feedback.dto.response.MyFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.StatisticResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserFeedbackController implements UserFeedbackApi {

    private final UserFeedbackService userFeedbackService;
    private final FeedbackLikeService feedbackLikeService;
    private final FeedbackStatisticService feedbackStatisticService;

    @Override
    public SuccessResponse<UserFeedbackListResponse> getUserFeedbacks(
            final String organizationUuid,
            final int size,
            final Long cursorId,
            final ProcessStatus status,
            final FeedbackOrderBy orderBy
    ) {
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                organizationUuid,
                size,
                cursorId,
                status,
                orderBy
        );
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<CreateFeedbackResponse> create(
            final String organizationUuid,
            final CreateFeedbackRequest request
    ) {
        final CreateFeedbackResponse response = userFeedbackService.create(request, organizationUuid);
        return SuccessResponse.success(HttpStatus.CREATED, response);
    }

    @Override
    public SuccessResponse<LikeResponse> like(final Long feedbackId) {
        final LikeResponse response = feedbackLikeService.like(feedbackId);
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<LikeResponse> unlike(final Long feedbackId) {
        final LikeResponse response = feedbackLikeService.unLike(feedbackId);
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<StatisticResponse> getStatistic(final String organizationUuid) {
        final StatisticResponse response = feedbackStatisticService.calculateStatistic(
                organizationUuid
        );
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<MyFeedbackListResponse> getMyFeedbacks(
            final String organizationUuid,
            final FeedbackOrderBy orderBy,
            final List<Long> feedbackIds
    ) {
        final MyFeedbackListResponse response = userFeedbackService.getMyFeedbackPage(
                organizationUuid,
                orderBy,
                feedbackIds
        );
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
