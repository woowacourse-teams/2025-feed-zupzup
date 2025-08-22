package feedzupzup.backend.qr.controller;

import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.organizer.dto.LoginOrganizerInfo;
import feedzupzup.backend.qr.api.QRApi;
import feedzupzup.backend.qr.dto.response.QRDownloadUrlResponse;
import feedzupzup.backend.qr.dto.response.QRResponse;
import feedzupzup.backend.qr.service.QRService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QRController implements QRApi {

    private final QRService qrService;

    public SuccessResponse<QRResponse> getQR(
            final LoginOrganizerInfo loginOrganizerInfo,
            final UUID organizationUuid
    ) {
        final QRResponse response = qrService.getQRCode(loginOrganizerInfo.organizationUuid());
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    public SuccessResponse<QRDownloadUrlResponse> getQRDownloadUrl(
            final LoginOrganizerInfo loginOrganizerInfo,
            final UUID organizationUuid
    ) {
        final QRDownloadUrlResponse response = qrService.getDownloadUrl(loginOrganizerInfo.organizationUuid());
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
