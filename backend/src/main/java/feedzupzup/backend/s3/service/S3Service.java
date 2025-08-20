package feedzupzup.backend.s3.service;

import feedzupzup.backend.s3.config.S3Properties;
import feedzupzup.backend.s3.dto.response.PresignedUrlResponse;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    public PresignedUrlResponse requestPresignedUrl(
            final Long objectId,
            final String extension,
            final String objectDir
    ) {
        final PutObjectPresignRequest objectPresignRequest = build(objectId, extension, objectDir);

        final String presignedUrl = s3Presigner.presignPutObject(objectPresignRequest)
                .url()
                .toExternalForm();
        return new PresignedUrlResponse(presignedUrl, S3ObjectType.fromExtension(extension).getContentType());
    }

    private PutObjectPresignRequest build(
            final Long objectId,
            final String extension,
            final String objectDir
    ) {
        final String objectKey = getObjectKey(objectId, extension, objectDir);

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

    private String getObjectKey(
            final Long objectId,
            final String extension,
            final String objectDir
    ) {
        return String.format("%s/%s/%s/%d/%s.%s",
                s3Properties.rootDirName(),
                s3Properties.environment(),
                objectDir,
                objectId,
                UUID.randomUUID(),
                extension
        );
    }
}
