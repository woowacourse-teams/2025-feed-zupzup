package feedzupzup.backend.s3.service;

import feedzupzup.backend.s3.config.S3Properties;
import feedzupzup.backend.s3.dto.request.FileUploadRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public String uploadFile(
            final FileUploadRequest fileUploadRequest
    ) {
        final String objectKey = generateObjectKey(
                fileUploadRequest.extension(),
                fileUploadRequest.objectDir(),
                UUID.fromString(fileUploadRequest.objectId())
        );
        putObject(objectKey, fileUploadRequest.extension(), fileUploadRequest.fileData());
        return generateObjectUrl(objectKey);
    }

    private String generateObjectKey(
            final String extension,
            final String objectDir,
            final UUID uuid
    ) {
        return String.format("%s/%s/%s/%s.%s",
                s3Properties.rootDirName(),
                s3Properties.environmentDir(),
                objectDir,
                uuid.toString(),
                extension
        );
    }

    private void putObject(
            final String objectKey,
            final String extension,
            final byte[] fileData
    ) {
        final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.bucketName())
                .key(objectKey)
                .contentType(S3ObjectType.fromExtension(extension).getContentType())
                .contentLength((long) fileData.length)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));
    }

    private String generateObjectUrl(final String objectKey) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                s3Properties.bucketName(),
                s3Properties.region(),
                objectKey
        );
    }
}
