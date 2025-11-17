package feedzupzup.backend.s3.service;

import feedzupzup.backend.s3.config.S3Properties;
import feedzupzup.backend.s3.exception.S3DownloadException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
public class S3DownloadService {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public byte[] downloadFile(final String imageUrl) {
        final String objectKey = extractObjectKeyFromUrl(imageUrl);
        return getObject(objectKey);
    }

    private String extractObjectKeyFromUrl(final String imageUrl) {
        final String urlPrefix = String.format("https://%s.s3.%s.amazonaws.com/",
                s3Properties.bucketName(),
                s3Properties.region()
        );

        if (!imageUrl.startsWith(urlPrefix)) {
            throw new S3DownloadException("유효하지 않은 S3 URL입니다: " + imageUrl);
        }

        return imageUrl.substring(urlPrefix.length());
    }

    private byte[] getObject(final String objectKey) {
        final GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Properties.bucketName())
                .key(objectKey)
                .build();

        try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest)) {
            return response.readAllBytes();
        } catch (S3Exception e) {
            throw new S3DownloadException("S3 서버 오류로 파일 다운로드에 실패했습니다: " + objectKey);
        } catch (SdkClientException e) {
            throw new S3DownloadException("클라이언트 오류로 파일 다운로드에 실패했습니다: " + objectKey);
        } catch (IOException e) {
            throw new S3DownloadException("파일 읽기 중 오류가 발생했습니다: " + objectKey);
        } catch (Exception e) {
            throw new S3DownloadException("파일 다운로드에 실패했습니다: " + objectKey);
        }
    }
}
