package feedzupzup.backend.s3.service;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.s3.config.S3Properties;
import feedzupzup.backend.s3.config.S3ServiceIntegrationHelper;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

class S3PresignedDownloadServiceTest extends S3ServiceIntegrationHelper {

    @Autowired
    private S3PresignedDownloadService s3PresignedDownloadService;

    @Autowired
    private S3Properties s3Properties;

    @Autowired
    private S3Client s3Client;

    private String testObjectKey;
    private String testImageUrl;

    @BeforeEach
    void setUpTestFile() {
        // 테스트용 파일 업로드
        testObjectKey = String.format("%s/%s/test/test-image.png",
                s3Properties.rootDirName(),
                s3Properties.environmentDir());

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(s3Properties.bucketName())
                        .key(testObjectKey)
                        .contentType("image/png")
                        .build(),
                RequestBody.fromString("test image content", StandardCharsets.UTF_8)
        );

        // S3 이미지 URL 생성
        testImageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                s3Properties.bucketName(),
                s3Properties.region(),
                testObjectKey);
    }

    @Test
    @DisplayName("이미지 URL로 다운로드 URL을 생성한다")
    void generateDownloadUrlFromImageUrl_Success() {
        // given
        final String filename = "download-image.png";

        // when
        final String downloadUrl = s3PresignedDownloadService.generateDownloadUrlFromImageUrl(testImageUrl, filename);

        // then
        assertThat(downloadUrl).contains(s3Properties.bucketName());
        assertThat(downloadUrl).contains(testObjectKey);
        assertThat(downloadUrl).contains("response-content-disposition");
        assertThat(downloadUrl).contains(filename);
    }
}
