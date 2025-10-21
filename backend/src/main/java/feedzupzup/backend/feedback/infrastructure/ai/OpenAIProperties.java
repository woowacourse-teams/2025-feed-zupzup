package feedzupzup.backend.feedback.infrastructure.ai;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openai.api")
@Getter
@Setter
public class OpenAIProperties {
    private String completionUrl;
    private String key;
    private String completionModel;
    private Integer maxTokens;
    private Double temperature;
    private Duration connectTimeout;
    private Duration readTimeout;
}
