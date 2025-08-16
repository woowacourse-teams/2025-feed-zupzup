package feedzupzup.backend.s3.controller;

import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.s3.api.S3Api;
import feedzupzup.backend.s3.dto.request.PresignedUrlRequest;
import feedzupzup.backend.s3.dto.response.PresignedUrlResponse;
import feedzupzup.backend.s3.service.S3PresignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class S3Controller implements S3Api {

    private final S3PresignService s3PresignService;

    public SuccessResponse<PresignedUrlResponse> generatePresignedUrl(final PresignedUrlRequest request) {
        final PresignedUrlResponse response = s3PresignService.requestPresignedUrl(
                request.objectId(), request.extension(), request.objectDir()
        );
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
