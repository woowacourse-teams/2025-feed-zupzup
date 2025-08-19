package feedzupzup.backend.s3.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import feedzupzup.backend.global.response.ErrorCode;
import feedzupzup.backend.s3.config.S3Properties;
import feedzupzup.backend.s3.exception.S3PresignedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@ExtendWith(MockitoExtension.class)
class S3PresignedDownloadServiceTest {

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private S3Properties s3Properties;

    @InjectMocks
    private S3PresignedDownloadService s3PresignedDownloadService;

    @Test
    @DisplayName("S3Exception 발생 시 S3PresignedException으로 변환한다")
    void generateDownloadUrl_S3Exception_ThrowsS3PresignedException() {
        // given
        when(s3Properties.bucketName()).thenReturn("test-bucket");
        when(s3Properties.signatureDuration()).thenReturn(5);
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenThrow(S3Exception.builder()
                        .message("S3 서버 오류")
                        .build());

        // when & then
        assertThatThrownBy(() -> s3PresignedDownloadService.generateDownloadUrl("test-key", "test.png"))
                .isInstanceOf(S3PresignedException.class)
                .hasMessageContaining("S3 서버 오류로 파일 다운로드 URL 생성에 실패했습니다");
    }

    @Test
    @DisplayName("SdkClientException 발생 시 S3PresignedException으로 변환한다")
    void generateDownloadUrl_SdkClientException_ThrowsS3PresignedException() {
        // given
        when(s3Properties.bucketName()).thenReturn("test-bucket");
        when(s3Properties.signatureDuration()).thenReturn(5);
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenThrow(SdkClientException.builder()
                        .message("클라이언트 오류")
                        .build());

        // when & then
        assertThatThrownBy(() -> s3PresignedDownloadService.generateDownloadUrl("test-key", "test.png"))
                .isInstanceOf(S3PresignedException.class)
                .hasMessageContaining("클라이언트 오류로 파일 다운로드 URL 생성에 실패했습니다");
    }

    @Test
    @DisplayName("S3PresignedException이 올바른 ErrorCode를 반환한다")
    void s3PresignedException_ReturnsCorrectErrorCode() {
        // given
        final S3PresignedException exception = new S3PresignedException("테스트 메시지");

        // when & then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.S3_UPLOAD_FAILED);
    }
}