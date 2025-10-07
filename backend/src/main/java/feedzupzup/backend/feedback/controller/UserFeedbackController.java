package feedzupzup.backend.feedback.controller;

import feedzupzup.backend.auth.presentation.annotation.Visitor;
import feedzupzup.backend.feedback.api.UserFeedbackApi;
import feedzupzup.backend.feedback.application.FeedbackLikeService;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortType;
import feedzupzup.backend.feedback.application.FeedbackStatisticService;
import feedzupzup.backend.feedback.application.UserFeedbackService;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.LikeHistoryResponse;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import feedzupzup.backend.feedback.dto.response.MyFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.StatisticResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.guest.domain.guest.Guest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;
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
            final UUID organizationUuid,
            final int size,
            final Long cursorId,
            final ProcessStatus status,
            final FeedbackSortType sortBy
    ) {
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                organizationUuid,
                size,
                cursorId,
                status,
                sortBy
        );
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<CreateFeedbackResponse> create(
            final UUID organizationUuid,
            final CreateFeedbackRequest request
    ) {
        final CreateFeedbackResponse response = userFeedbackService.create(request,
                organizationUuid);
        return SuccessResponse.success(HttpStatus.CREATED, response);
    }

    @Override
    public SuccessResponse<LikeResponse> like(
            final HttpServletResponse response,
            final Long feedbackId,
            @Visitor final Guest guest
    ) {
        final LikeResponse likeResponse = feedbackLikeService.like(feedbackId, guest);
        return SuccessResponse.success(HttpStatus.OK, likeResponse);
    }

    @Override
    public SuccessResponse<LikeResponse> unlike(
            final HttpServletResponse response,
            final Long feedbackId,
            @Visitor final Guest guest
    ) {
        final LikeResponse likeResponse = feedbackLikeService.unlike(feedbackId, guest.getVisitorUuid());
        return SuccessResponse.success(HttpStatus.OK, likeResponse);
    }

    @Override
    public SuccessResponse<StatisticResponse> getStatistic(final UUID organizationUuid) {
        final StatisticResponse response = feedbackStatisticService.calculateStatistic(
                organizationUuid
        );
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<LikeHistoryResponse> getMyLikeHistories(
            final HttpServletResponse httpServletResponse,
            @Visitor final Guest guest
    ) {
        final LikeHistoryResponse response = feedbackLikeService.findLikeHistories(
                guest.getVisitorUuid());
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<MyFeedbackListResponse> getMyFeedbacks(
            final UUID organizationUuid,
            final FeedbackSortType sortBy,
            final List<Long> feedbackIds
    ) {
        final MyFeedbackListResponse response = userFeedbackService.getMyFeedbackPage(
                organizationUuid,
                sortBy,
                feedbackIds
        );
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
