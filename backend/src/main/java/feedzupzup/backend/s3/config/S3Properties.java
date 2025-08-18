package feedzupzup.backend.s3.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "s3")
public record S3Properties(
        String region,
        String bucketName,
        String rootDirName,
        String environmentDir,
        int signatureDuration
) {

}
