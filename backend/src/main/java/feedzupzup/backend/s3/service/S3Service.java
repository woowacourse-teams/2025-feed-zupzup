package feedzupzup.backend.s3.service;

import static feedzupzup.backend.s3.service.S3Constants.ROOT_DIR_NAME;
import static feedzupzup.backend.s3.service.S3Constants.SIGNATURE_DURATION;

import feedzupzup.backend.s3.dto.response.PresignedUrlResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@RequiredArgsConstructor
@Component
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${s3.bucket-name}")
    private String bucketName;
    @Value("${s3.environment}")
    public String environment;

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
                .bucket(bucketName)
                .key(objectKey)
                .contentType(S3ObjectType.fromExtension(extension).getContentType())
                .build();

        return PutObjectPresignRequest.builder()
                .signatureDuration(SIGNATURE_DURATION)
                .putObjectRequest(putObjectRequest)
                .build();
    }

    private String getObjectKey(
            final Long objectId,
            final String extension,
            final String objectDir
    ) {
        return String.format("%s/%s/%s/%d/%s.%s",
                ROOT_DIR_NAME,
                environment,
                objectDir,
                objectId,
                UUID.randomUUID(),
                extension
        );
    }
}
