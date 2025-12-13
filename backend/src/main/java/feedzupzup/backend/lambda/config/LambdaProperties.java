package feedzupzup.backend.lambda.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lambda")
public record LambdaProperties(
        String region,
        String feedbackExcelFunctionName
) {

}
