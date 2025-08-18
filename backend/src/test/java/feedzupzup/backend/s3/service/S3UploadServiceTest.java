package feedzupzup.backend.s3.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import feedzupzup.backend.global.response.ErrorCode;
import feedzupzup.backend.qr.dto.request.QRCodeUploadRequest;
import feedzupzup.backend.s3.config.S3Properties;
import feedzupzup.backend.s3.exception.S3UploadException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@ExtendWith(MockitoExtension.class)
class S3UploadServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Properties s3Properties;

    @InjectMocks
    private S3UploadService s3UploadService;

    @Test
    @DisplayName("S3Exception 발생 시 S3UploadException으로 변환한다")
    void uploadFile_S3Exception_ThrowsS3UploadException() {
        // given
        when(s3Properties.rootDirName()).thenReturn("root");
        when(s3Properties.environmentDir()).thenReturn("dev");
        when(s3Properties.bucketName()).thenReturn("test-bucket");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder()
                        .message("S3 서버 오류")
                        .build());

        final QRCodeUploadRequest request = new QRCodeUploadRequest(
                "png", "test", "12345678-1234-1234-1234-123456789012", new byte[]{1, 2, 3}
        );

        // when & then
        assertThatThrownBy(() -> s3UploadService.uploadFile(request))
                .isInstanceOf(S3UploadException.class)
                .hasMessageContaining("S3 서버 오류로 파일 업로드에 실패했습니다");
    }

    @Test
    @DisplayName("SdkClientException 발생 시 S3UploadException으로 변환한다")
    void uploadFile_SdkClientException_ThrowsS3UploadException() {
        // given
        when(s3Properties.rootDirName()).thenReturn("root");
        when(s3Properties.environmentDir()).thenReturn("dev");
        when(s3Properties.bucketName()).thenReturn("test-bucket");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(SdkClientException.builder()
                        .message("클라이언트 오류")
                        .build());

        final QRCodeUploadRequest request = new QRCodeUploadRequest(
                "png", "test", "12345678-1234-1234-1234-123456789012", new byte[]{1, 2, 3}
        );

        // when & then
        assertThatThrownBy(() -> s3UploadService.uploadFile(request))
                .isInstanceOf(S3UploadException.class)
                .hasMessageContaining("S3 클라이언트 오류로 파일 업로드에 실패했습니다");
    }

    @Test
    @DisplayName("S3UploadException이 올바른 ErrorCode를 반환한다")
    void s3UploadException_ReturnsCorrectErrorCode() {
        // given
        final S3UploadException exception = new S3UploadException("테스트 메시지");

        // when & then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.S3_UPLOAD_FAILED);
    }
}
