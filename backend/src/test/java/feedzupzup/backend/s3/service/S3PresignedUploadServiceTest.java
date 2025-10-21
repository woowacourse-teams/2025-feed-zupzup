package feedzupzup.backend.s3.service;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.s3.config.S3Properties;
import feedzupzup.backend.s3.config.S3ServiceIntegrationHelper;
import feedzupzup.backend.s3.dto.response.PresignedUrlResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class S3PresignedUploadServiceTest extends S3ServiceIntegrationHelper {

    @Autowired
    private S3PresignedUploadService s3PresignedUploadService;

    @Autowired
    private S3Properties s3Properties;

    @Test
    @DisplayName("Presigned URL을 생성한다")
    void requestPresignedUrl_Success() {
        // given
        final String objectDir = "feedback_media";
        final String extension = "png";

        // when
        final PresignedUrlResponse response = s3PresignedUploadService.requestPresignedUrl(objectDir, extension);

        // then
        final String expectedPattern = String.format(".*%s/%s/%s/%s/.*\\.%s.*",
                s3Properties.bucketName(),
                s3Properties.rootDirName(),
                s3Properties.environmentDir(),
                objectDir,
                extension);
        assertThat(response.presignedUrl()).matches(expectedPattern);
        assertThat(response.contentType()).isEqualTo("image/png");
    }

    @Test
    @DisplayName("매번 다른 Presigned URL을 생성한다")
    void requestPresignedUrl_GeneratesUniqueUrls() {
        // when
        final PresignedUrlResponse response1 = s3PresignedUploadService.requestPresignedUrl("test", "png");
        final PresignedUrlResponse response2 = s3PresignedUploadService.requestPresignedUrl("test", "png");

        // then - UUID가 포함되어 있어 매번 다른 URL 생성
        assertThat(response1.presignedUrl()).isNotEqualTo(response2.presignedUrl());
    }
}
