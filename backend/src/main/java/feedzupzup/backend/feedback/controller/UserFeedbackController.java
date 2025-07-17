package feedzupzup.backend.feedback.controller;

import feedzupzup.backend.feedback.api.UserFeedbackApi;
import feedzupzup.backend.feedback.application.FeedbackService;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import feedzupzup.backend.feedback.dto.response.UnLikeResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserFeedbackController implements UserFeedbackApi {

    private final FeedbackService feedbackService;

    @Override
    public SuccessResponse<UserFeedbackListResponse> getUserFeedbacks(Long placeId, int size, Long cursorId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SuccessResponse<CreateFeedbackResponse> create(Long placeId, CreateFeedbackRequest request) {
        final CreateFeedbackResponse response = feedbackService.create(request, placeId);
        return SuccessResponse.success(HttpStatus.CREATED, response);
    }

    @Override
    public SuccessResponse<LikeResponse> like(Long feedbackId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SuccessResponse<UnLikeResponse> unlike(Long feedbackId) {
        throw new UnsupportedOperationException();
    }
}
