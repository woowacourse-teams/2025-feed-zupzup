package feedzupzup.backend.feedback.controller;

import feedzupzup.backend.auth.presentation.annotation.SavedGuest;
import feedzupzup.backend.auth.presentation.annotation.VisitedGuest;
import feedzupzup.backend.feedback.api.UserFeedbackApi;
import feedzupzup.backend.feedback.application.FeedbackLikeService;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortType;
import feedzupzup.backend.feedback.application.UserFeedbackService;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.guest.dto.GuestInfo;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
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
            final UUID organizationUuid,
            final int size,
            final Long cursorId,
            final ProcessStatus status,
            final FeedbackSortType sortBy,
            final @VisitedGuest GuestInfo guestInfo
    ) {
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                organizationUuid,
                size,
                cursorId,
                status,
                sortBy,
                guestInfo
        );
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<CreateFeedbackResponse> create(
            final UUID organizationUuid,
            final CreateFeedbackRequest request,
            @SavedGuest final GuestInfo guestInfo
    ) {
        final CreateFeedbackResponse response = userFeedbackService.create(request,
                organizationUuid, guestInfo);
        return SuccessResponse.success(HttpStatus.CREATED, response);
    }

    @Override
    public SuccessResponse<LikeResponse> like(
            final HttpServletResponse response,
            final Long feedbackId,
            @SavedGuest final GuestInfo guestInfo
    ) {
        final LikeResponse likeResponse = feedbackLikeService.like(feedbackId, guestInfo);
        return SuccessResponse.success(HttpStatus.OK, likeResponse);
    }

    @Override
    public SuccessResponse<LikeResponse> unlike(
            final HttpServletResponse response,
            final Long feedbackId,
            @SavedGuest final GuestInfo guestInfo
    ) {
        final LikeResponse likeResponse = feedbackLikeService.unlike(feedbackId, guestInfo);
        return SuccessResponse.success(HttpStatus.OK, likeResponse);
    }
}
