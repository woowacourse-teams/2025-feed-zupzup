package feedzupzup.backend.s3.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@ExtendWith(MockitoExtension.class)
class S3PresignedUploadServiceUnitTest {

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private S3Properties s3Properties;

    @InjectMocks
    private S3PresignedUploadService s3PresignedUploadService;

    @Test
    @DisplayName("S3Exception 발생 시 S3PresignedException으로 추상화한다")
    void requestPresignedUrl_S3Exception_ThrowsS3PresignedException() {
        // given
        when(s3Properties.bucketName()).thenReturn("test-bucket");
        when(s3Properties.rootDirName()).thenReturn("root");
        when(s3Properties.environmentDir()).thenReturn("dev");
        when(s3Properties.signatureDuration()).thenReturn(10);
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenThrow(S3Exception.builder().message("S3 서버 오류").build());

        // when & then
        assertThatThrownBy(() -> s3PresignedUploadService.requestPresignedUrl("test-dir", "png"))
                .isInstanceOf(S3PresignedException.class)
                .hasMessageContaining("S3 서버 오류로 파일 업로드 URL 생성에 실패했습니다");
    }

    @Test
    @DisplayName("SdkClientException 발생 시 S3PresignedException으로 추상화한다")
    void requestPresignedUrl_SdkClientException_ThrowsS3PresignedException() {
        // given
        when(s3Properties.bucketName()).thenReturn("test-bucket");
        when(s3Properties.rootDirName()).thenReturn("root");
        when(s3Properties.environmentDir()).thenReturn("dev");
        when(s3Properties.signatureDuration()).thenReturn(10);
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenThrow(SdkClientException.builder().message("클라이언트 오류").build());

        // when & then
        assertThatThrownBy(() -> s3PresignedUploadService.requestPresignedUrl("test-dir", "png"))
                .isInstanceOf(S3PresignedException.class)
                .hasMessageContaining("클라이언트 오류로 파일 업로드 URL 생성에 실패했습니다");
    }

    @Test
    @DisplayName("일반 Exception 발생 시 S3PresignedException으로 추상화한다")
    void requestPresignedUrl_GeneralException_ThrowsS3PresignedException() {
        // given
        when(s3Properties.bucketName()).thenReturn("test-bucket");
        when(s3Properties.rootDirName()).thenReturn("root");
        when(s3Properties.environmentDir()).thenReturn("dev");
        when(s3Properties.signatureDuration()).thenReturn(10);
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenThrow(new RuntimeException("예상치 못한 오류"));

        // when & then
        assertThatThrownBy(() -> s3PresignedUploadService.requestPresignedUrl("test-dir", "png"))
                .isInstanceOf(S3PresignedException.class)
                .hasMessageContaining("파일 업로드 URL 생성에 실패했습니다");
    }
}
