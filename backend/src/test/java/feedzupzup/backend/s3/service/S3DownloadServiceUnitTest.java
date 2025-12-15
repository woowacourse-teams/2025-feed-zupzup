package feedzupzup.backend.s3.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import feedzupzup.backend.s3.config.S3Properties;
import feedzupzup.backend.s3.exception.S3DownloadException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@ExtendWith(MockitoExtension.class)
class S3DownloadServiceUnitTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Properties s3Properties;

    @InjectMocks
    private S3DownloadService s3DownloadService;

    private static final String BUCKET_NAME = "test-bucket";
    private static final String REGION = "ap-northeast-2";
    private static final String OBJECT_KEY = "test/image.png";
    private static final String VALID_IMAGE_URL = String.format(
            "https://%s.s3.%s.amazonaws.com/%s",
            BUCKET_NAME, REGION, OBJECT_KEY
    );

    @BeforeEach
    void setUp() {
        when(s3Properties.bucketName()).thenReturn(BUCKET_NAME);
        when(s3Properties.region()).thenReturn(REGION);
    }

    @Test
    @DisplayName("정상적으로 파일을 다운로드한다")
    void downloadFile_Success() {
        // given
        byte[] expectedBytes = new byte[]{1, 2, 3, 4, 5};
        ResponseInputStream<GetObjectResponse> responseInputStream = createResponseInputStream(expectedBytes);

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseInputStream);

        // when
        byte[] actualBytes = s3DownloadService.downloadFile(VALID_IMAGE_URL);

        // then
        assertThat(actualBytes).isEqualTo(expectedBytes);
    }

    @Test
    @DisplayName("유효하지 않은 S3 URL로 다운로드 시 예외를 발생시킨다")
    void downloadFile_InvalidUrl_ThrowsException() {
        // given
        String invalidUrl = "https://invalid-url.com/image.png";

        // when & then
        assertThatThrownBy(() -> s3DownloadService.downloadFile(invalidUrl))
                .isInstanceOf(S3DownloadException.class)
                .hasMessageContaining("유효하지 않은 S3 URL입니다");
    }

    @Test
    @DisplayName("잘못된 버킷 이름이 포함된 URL로 다운로드 시 예외를 발생시킨다")
    void downloadFile_WrongBucketName_ThrowsException() {
        // given
        String wrongBucketUrl = String.format(
                "https://wrong-bucket.s3.%s.amazonaws.com/%s",
                REGION, OBJECT_KEY
        );

        // when & then
        assertThatThrownBy(() -> s3DownloadService.downloadFile(wrongBucketUrl))
                .isInstanceOf(S3DownloadException.class)
                .hasMessageContaining("유효하지 않은 S3 URL입니다");
    }

    @Test
    @DisplayName("잘못된 리전이 포함된 URL로 다운로드 시 예외를 발생시킨다")
    void downloadFile_WrongRegion_ThrowsException() {
        // given
        String wrongRegionUrl = String.format(
                "https://%s.s3.us-east-1.amazonaws.com/%s",
                BUCKET_NAME, OBJECT_KEY
        );

        // when & then
        assertThatThrownBy(() -> s3DownloadService.downloadFile(wrongRegionUrl))
                .isInstanceOf(S3DownloadException.class)
                .hasMessageContaining("유효하지 않은 S3 URL입니다");
    }

    @Test
    @DisplayName("S3Exception 발생 시 S3DownloadException으로 변환한다")
    void downloadFile_S3Exception_ThrowsS3DownloadException() {
        // given
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(S3Exception.builder()
                        .message("S3 서버 오류")
                        .build());

        // when & then
        assertThatThrownBy(() -> s3DownloadService.downloadFile(VALID_IMAGE_URL))
                .isInstanceOf(S3DownloadException.class)
                .hasMessageContaining("S3 서버 오류로 파일 다운로드에 실패했습니다");
    }

    @Test
    @DisplayName("SdkClientException 발생 시 S3DownloadException으로 변환한다")
    void downloadFile_SdkClientException_ThrowsS3DownloadException() {
        // given
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(SdkClientException.builder()
                        .message("클라이언트 오류")
                        .build());

        // when & then
        assertThatThrownBy(() -> s3DownloadService.downloadFile(VALID_IMAGE_URL))
                .isInstanceOf(S3DownloadException.class)
                .hasMessageContaining("클라이언트 오류로 파일 다운로드에 실패했습니다");
    }

    @Test
    @DisplayName("IOException 발생 시 S3DownloadException으로 변환한다")
    void downloadFile_IOException_ThrowsS3DownloadException() throws IOException {
        // given
        ResponseInputStream<GetObjectResponse> mockInputStream = mock(ResponseInputStream.class);
        when(mockInputStream.readAllBytes()).thenThrow(new IOException("파일 읽기 오류"));
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockInputStream);

        // when & then
        assertThatThrownBy(() -> s3DownloadService.downloadFile(VALID_IMAGE_URL))
                .isInstanceOf(S3DownloadException.class)
                .hasMessageContaining("파일 읽기 중 오류가 발생했습니다");
    }

    @Test
    @DisplayName("일반 예외 발생 시 S3DownloadException으로 변환한다")
    void downloadFile_GeneralException_ThrowsS3DownloadException() {
        // given
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(new RuntimeException("예상치 못한 오류"));

        // when & then
        assertThatThrownBy(() -> s3DownloadService.downloadFile(VALID_IMAGE_URL))
                .isInstanceOf(S3DownloadException.class)
                .hasMessageContaining("파일 다운로드에 실패했습니다");
    }

    private ResponseInputStream<GetObjectResponse> createResponseInputStream(byte[] content) {
        GetObjectResponse response = GetObjectResponse.builder().build();
        AbortableInputStream abortableInputStream = AbortableInputStream.create(
                new ByteArrayInputStream(content)
        );
        return new ResponseInputStream<>(response, abortableInputStream);
    }
}