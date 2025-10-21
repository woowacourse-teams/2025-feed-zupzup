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
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@ExtendWith(MockitoExtension.class)
class S3PresignedDownloadServiceUnitTest {

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private S3Properties s3Properties;

    @InjectMocks
    private S3PresignedDownloadService s3PresignedDownloadService;

    @Test
    @DisplayName("S3Exception 발생 시 S3PresignedException으로 변환한다")
    void generateDownloadUrlFromImageUrl_S3Exception_ThrowsS3PresignedException() {
        // given
        final String imageUrl = "https://test-bucket.s3.ap-northeast-2.amazonaws.com/test-key";
        when(s3Properties.bucketName()).thenReturn("test-bucket");
        when(s3Properties.region()).thenReturn("ap-northeast-2");
        when(s3Properties.signatureDuration()).thenReturn(5);
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenThrow(S3Exception.builder()
                        .message("S3 서버 오류")
                        .build());

        // when & then
        assertThatThrownBy(() -> s3PresignedDownloadService.generateDownloadUrlFromImageUrl(imageUrl, "test.png"))
                .isInstanceOf(S3PresignedException.class)
                .hasMessageContaining("S3 서버 오류로 파일 다운로드 URL 생성에 실패했습니다");
    }

    @Test
    @DisplayName("SdkClientException 발생 시 S3PresignedException으로 변환한다")
    void generateDownloadUrlFromImageUrl_SdkClientException_ThrowsS3PresignedException() {
        // given
        final String imageUrl = "https://test-bucket.s3.ap-northeast-2.amazonaws.com/test-key";
        when(s3Properties.bucketName()).thenReturn("test-bucket");
        when(s3Properties.region()).thenReturn("ap-northeast-2");
        when(s3Properties.signatureDuration()).thenReturn(5);
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenThrow(SdkClientException.builder()
                        .message("클라이언트 오류")
                        .build());

        // when & then
        assertThatThrownBy(() -> s3PresignedDownloadService.generateDownloadUrlFromImageUrl(imageUrl, "test.png"))
                .isInstanceOf(S3PresignedException.class)
                .hasMessageContaining("클라이언트 오류로 파일 다운로드 URL 생성에 실패했습니다");
    }

    @Test
    @DisplayName("일반 Exception 발생 시 S3PresignedException으로 변환한다")
    void generateDownloadUrlFromImageUrl_GeneralException_ThrowsS3PresignedException() {
        // given
        final String imageUrl = "https://test-bucket.s3.ap-northeast-2.amazonaws.com/test-key";
        when(s3Properties.bucketName()).thenReturn("test-bucket");
        when(s3Properties.region()).thenReturn("ap-northeast-2");
        when(s3Properties.signatureDuration()).thenReturn(5);
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenThrow(new RuntimeException("예상치 못한 오류"));

        // when & then
        assertThatThrownBy(() -> s3PresignedDownloadService.generateDownloadUrlFromImageUrl(imageUrl, "test.png"))
                .isInstanceOf(S3PresignedException.class)
                .hasMessageContaining("파일 다운로드 URL 생성에 실패했습니다");
    }

    @Test
    @DisplayName("잘못된 이미지 URL 형식이면 예외를 발생시킨다")
    void generateDownloadUrlFromImageUrl_InvalidUrl_ThrowsException() {
        // given
        final String invalidImageUrl = "https://invalid-bucket.s3.ap-northeast-2.amazonaws.com/test.png";
        final String filename = "download.png";
        when(s3Properties.bucketName()).thenReturn("test-bucket");
        when(s3Properties.region()).thenReturn("ap-northeast-2");

        // when & then
        assertThatThrownBy(() -> s3PresignedDownloadService.generateDownloadUrlFromImageUrl(invalidImageUrl, filename))
                .isInstanceOf(S3PresignedException.class)
                .hasMessageContaining("올바르지 않은 S3 이미지 URL 형식입니다");
    }
}
