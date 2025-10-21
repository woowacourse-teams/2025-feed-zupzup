package feedzupzup.backend.s3.service;

import feedzupzup.backend.s3.config.S3Properties;
import feedzupzup.backend.s3.exception.S3PresignedException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3PresignedDownloadService {

    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    public String generateDownloadUrlFromImageUrl(final String imageUrl, final String filename) {
        final String objectKey = extractObjectKeyFromUrl(imageUrl);
        return generateDownloadUrl(objectKey, filename);
    }

    private String generateDownloadUrl(final String objectKey, final String filename) {
        final GetObjectPresignRequest presignRequest = build(objectKey, filename);

        try {
            return s3Presigner.presignGetObject(presignRequest)
                    .url()
                    .toExternalForm();
        } catch (final S3Exception e) {
            throw new S3PresignedException("S3 서버 오류로 파일 다운로드 URL 생성에 실패했습니다: " + objectKey);
        } catch (final SdkClientException e) {
            throw new S3PresignedException("클라이언트 오류로 파일 다운로드 URL 생성에 실패했습니다: " + objectKey);
        } catch (final Exception e) {
            throw new S3PresignedException("파일 다운로드 URL 생성에 실패했습니다: " + objectKey);
        }
    }

    private GetObjectPresignRequest build(final String objectKey, final String filename) {
        final GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Properties.bucketName())
                .key(objectKey)
                .responseContentDisposition("attachment; filename=\"" + filename + "\"")
                .build();

        return GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(s3Properties.signatureDuration()))
                .getObjectRequest(getObjectRequest)
                .build();
    }

    private String extractObjectKeyFromUrl(final String imageUrl) {
        final String bucketUrl = String.format("https://%s.s3.%s.amazonaws.com/",
                s3Properties.bucketName(),
                s3Properties.region());

        if (!imageUrl.startsWith(bucketUrl)) {
            throw new S3PresignedException("올바르지 않은 S3 이미지 URL 형식입니다: " + imageUrl);
        }

        return imageUrl.substring(bucketUrl.length());
    }
}
