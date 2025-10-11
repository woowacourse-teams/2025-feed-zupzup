package feedzupzup.backend.feedback.infrastructure.ai;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "openai.api")
@Getter
@Setter
public class OpenAIProperties {
    private String embeddingUrl;
    private String key;
    private String embeddingModel;
    private Duration connectTimeout;
    private Duration readTimeout;
}
