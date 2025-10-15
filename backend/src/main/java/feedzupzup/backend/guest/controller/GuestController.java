package feedzupzup.backend.guest.controller;

import feedzupzup.backend.auth.presentation.annotation.VisitedGuest;
import feedzupzup.backend.guest.dto.GuestInfo;
import feedzupzup.backend.guest.dto.response.LikeHistoryResponse;
import feedzupzup.backend.feedback.dto.response.MyFeedbackListResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.guest.api.GuestApi;
import feedzupzup.backend.guest.application.GuestService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GuestController implements GuestApi {

    private final GuestService guestService;

    @Override
    public SuccessResponse<MyFeedbackListResponse> getMyFeedbacks(
            final UUID organizationUuid,
            @VisitedGuest final GuestInfo guestInfo
    ) {
        final MyFeedbackListResponse response = guestService.getMyFeedbackPage(
                organizationUuid, guestInfo);
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<LikeHistoryResponse> getMyLikeHistories(
            final UUID organizationUuid,
            @VisitedGuest final GuestInfo guestInfo
    ) {
        final LikeHistoryResponse response = guestService.findGuestLikeHistories(
                organizationUuid, guestInfo);
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
