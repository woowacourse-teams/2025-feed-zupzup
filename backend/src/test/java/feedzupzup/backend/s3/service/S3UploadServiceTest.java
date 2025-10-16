package feedzupzup.backend.s3.service;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.s3.config.S3Properties;
import feedzupzup.backend.s3.config.S3ServiceIntegrationHelper;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

class S3UploadServiceTest extends S3ServiceIntegrationHelper {

    @Autowired
    private S3UploadService s3UploadService;

    @Autowired
    private S3Properties s3Properties;

    @Autowired
    private S3Client s3Client;

    @Test
    @DisplayName("파일을 S3에 업로드하고 URL을 반환한다")
    void uploadFile_Success() {
        // given
        final String objectId = UUID.randomUUID().toString();
        final String objectDir = "test-dir";
        final String extension = "png";
        final byte[] fileData = "test file content".getBytes(StandardCharsets.UTF_8);

        // when
        final String uploadedUrl = s3UploadService.uploadFile(extension, objectDir, objectId, fileData);

        // then
        final String expectedPattern = String.format("^https?://.*?%s(?:/|\\.).*?/%s/%s/%s/%s\\.%s$",
                s3Properties.bucketName(),
                s3Properties.rootDirName(),
                s3Properties.environmentDir(),
                objectDir,
                objectId,
                extension);
        assertThat(uploadedUrl).matches(expectedPattern);

    }

    @Test
    @DisplayName("업로드된 파일이 S3에 정상적으로 저장된다")
    void uploadFile_FileIsStoredInS3() {
        // given
        final String objectId = UUID.randomUUID().toString();
        final String objectDir = "test-dir";
        final String extension = "png";
        final String fileContent = "test file content";
        final byte[] fileData = fileContent.getBytes(StandardCharsets.UTF_8);

        // when
        s3UploadService.uploadFile(extension, objectDir, objectId, fileData);

        // then - S3에서 파일을 읽어서 내용 확인
        final String objectKey = String.format("%s/%s/%s/%s.%s",
                s3Properties.rootDirName(),
                s3Properties.environmentDir(),
                objectDir,
                objectId,
                extension);

        final ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(s3Properties.bucketName())
                        .key(objectKey)
                        .build()
        );

        final String storedContent = responseBytes.asUtf8String();
        assertThat(storedContent).isEqualTo(fileContent);
    }
}
