package feedzupzup.backend.s3.config;

import feedzupzup.backend.config.ServiceIntegrationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;

@LocalStackS3Test
public abstract class S3ServiceIntegrationHelper extends ServiceIntegrationHelper {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Properties s3Properties;

    @BeforeEach
    void setUpS3() {
        final String bucketName = s3Properties.bucketName();

        // 버킷이 없다면 생성
        if (s3Client.listBuckets().buckets().stream()
                .noneMatch(b -> b.name().equals(bucketName))) {
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build());
        }

        // 버킷의 객체 정리
        s3Client.listObjectsV2(
                        ListObjectsV2Request.builder()
                                .bucket(bucketName)
                                .build()
                ).contents()
                .stream()
                .map(S3Object::key)
                .forEach(key -> s3Client.deleteObject(
                        DeleteObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .build()
                ));
    }
}
