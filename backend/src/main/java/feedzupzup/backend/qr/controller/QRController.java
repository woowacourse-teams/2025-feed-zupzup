package feedzupzup.backend.qr.controller;

import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.qr.api.QRApi;
import feedzupzup.backend.qr.dto.QRResponse;
import feedzupzup.backend.qr.service.QRService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class QRController implements QRApi {

    private final QRService qrService;

    public SuccessResponse<QRResponse> getQR(final UUID organizationUuid) {
        final QRResponse response = qrService.getQR(organizationUuid);
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
