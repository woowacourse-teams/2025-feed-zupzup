package feedzupzup.backend.feedback.controller;

import feedzupzup.backend.feedback.api.UserFeedbackApi;
import feedzupzup.backend.feedback.application.FeedbackLikeService;
import feedzupzup.backend.feedback.application.UserFeedbackService;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserFeedbackController implements UserFeedbackApi {

    private final UserFeedbackService userFeedbackService;
    private final FeedbackLikeService feedbackLikeService;

    @Override
    public SuccessResponse<UserFeedbackListResponse> getUserFeedbacks(
            final Long placeId,
            final int size,
            final Long cursorId
    ) {
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                placeId,
                size,
                cursorId
        );
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<CreateFeedbackResponse> create(final Long placeId, final CreateFeedbackRequest request) {
        final CreateFeedbackResponse response = userFeedbackService.create(request, placeId);
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
}
