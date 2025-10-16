package feedzupzup.backend.s3.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@TestConfiguration
public class LocalStackS3TestConfig {

    private static final DockerImageName LOCALSTACK_IMAGE =
            DockerImageName.parse("localstack/localstack:3.0");

    @Bean
    static LocalStackContainer localStackContainer() {
        final LocalStackContainer container = new LocalStackContainer(LOCALSTACK_IMAGE)
                .withServices(Service.S3)
                .withReuse(true);
        container.start();
        return container;
    }

    @Bean
    @Primary
    public S3Client testS3Client(final LocalStackContainer localStackContainer) {
        return S3Client.builder()
                .endpointOverride(localStackContainer.getEndpointOverride(Service.S3))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        localStackContainer.getAccessKey(),
                                        localStackContainer.getSecretKey()
                                )
                        )
                )
                .region(Region.of(localStackContainer.getRegion()))
                .forcePathStyle(true)
                .build();
    }

    @Bean
    @Primary
    public S3Presigner testS3Presigner(final LocalStackContainer localStackContainer) {
        return S3Presigner.builder()
                .endpointOverride(localStackContainer.getEndpointOverride(Service.S3))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        localStackContainer.getAccessKey(),
                                        localStackContainer.getSecretKey()
                                )
                        )
                )
                .region(Region.of(localStackContainer.getRegion()))
                .build();
    }
}
