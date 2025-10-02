package feedzupzup.backend.feedback.infrastructure.ai;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "openai.api")
@Getter
public class OpenAIProperties {
    private String embeddingUrl;
    private String key;
    private String embeddingModel;
}
