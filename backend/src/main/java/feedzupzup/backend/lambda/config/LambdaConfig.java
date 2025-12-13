package feedzupzup.backend.lambda.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;

@Configuration
@EnableConfigurationProperties(LambdaProperties.class)
public class LambdaConfig {

    @Bean
    public LambdaClient lambdaClient(final LambdaProperties properties) {
        return LambdaClient.builder()
                .region(Region.of(properties.region()))
                .build();
    }
}
