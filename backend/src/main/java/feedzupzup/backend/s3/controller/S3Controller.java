package feedzupzup.backend.s3.controller;

import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.s3.api.S3Api;
import feedzupzup.backend.s3.dto.request.PresignedUrlRequest;
import feedzupzup.backend.s3.dto.response.PresignedUrlResponse;
import feedzupzup.backend.s3.service.S3Constants;
import feedzupzup.backend.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class S3Controller implements S3Api {

    private final S3Service s3Service;

    public SuccessResponse<PresignedUrlResponse> generatePresignedUrl(final PresignedUrlRequest request) {
        final PresignedUrlResponse response = s3Service.requestPresignedUrl(
                request.feedbackId(), request.objectType(), S3Constants.FEEDBACK_OBJECT_DIR);
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
