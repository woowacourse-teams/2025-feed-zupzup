package feedzupzup.backend.s3.service;

import feedzupzup.backend.s3.config.S3Properties;
import feedzupzup.backend.s3.dto.response.PresignedUrlResponse;
import feedzupzup.backend.s3.exception.S3PresignedException;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3PresignedUploadService {

    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    public PresignedUrlResponse requestPresignedUrl(final String objectDir, final String extension) {
        final PutObjectPresignRequest objectPresignRequest = build(objectDir, extension);

        try {
            final String presignedUrl = s3Presigner.presignPutObject(objectPresignRequest)
                    .url()
                    .toExternalForm();
            return new PresignedUrlResponse(presignedUrl, S3ObjectType.fromExtension(extension).getContentType());
        } catch (final S3Exception e) {
            throw new S3PresignedException("S3 서버 오류로 파일 업로드 URL 생성에 실패했습니다: " + e.getMessage());
        } catch (final SdkClientException e) {
            throw new S3PresignedException("클라이언트 오류로 파일 업로드 URL 생성에 실패했습니다: " + e.getMessage());
        } catch (final Exception e) {
            throw new S3PresignedException("파일 업로드 URL 생성에 실패했습니다: " + e.getMessage());
        }
    }

    private PutObjectPresignRequest build(final String objectDir, final String extension) {
        final String objectKey = generateObjectKey(objectDir, extension);

        final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.bucketName())
                .key(objectKey)
                .contentType(S3ObjectType.fromExtension(extension).getContentType())
                .build();

        return PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(s3Properties.signatureDuration()))
                .putObjectRequest(putObjectRequest)
                .build();
    }

    private String generateObjectKey(final String objectDir, final String extension) {
        return String.format("%s/%s/%s/%s.%s",
                s3Properties.rootDirName(),
                s3Properties.environmentDir(),
                objectDir,
                UUID.randomUUID(),
                extension
        );
    }
}
