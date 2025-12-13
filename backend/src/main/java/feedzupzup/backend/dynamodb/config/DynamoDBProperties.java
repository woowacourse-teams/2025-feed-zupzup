package feedzupzup.backend.dynamodb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dynamodb")
public record DynamoDBProperties(
        String region,
        String feedbackDownloadJobTableName
) {

}
