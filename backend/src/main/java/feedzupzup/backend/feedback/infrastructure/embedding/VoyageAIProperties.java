package feedzupzup.backend.feedback.infrastructure.embedding;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "voyage.api")
@Getter
@Setter
public class VoyageAIProperties {
    private String embeddingUrl;
    private String key;
    private String embeddingModel;
    private Duration connectTimeout;
    private Duration readTimeout;
}
