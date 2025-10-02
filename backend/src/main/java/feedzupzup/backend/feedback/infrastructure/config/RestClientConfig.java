package feedzupzup.backend.feedback.infrastructure.config;

import feedzupzup.backend.feedback.infrastructure.ai.OpenAIProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final OpenAIProperties openAIProperties;

    @Bean
    public RestClient openAiEmbeddingRestClient() {
        return RestClient.builder()
                .baseUrl(openAIProperties.getEmbeddingUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAIProperties.getKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
