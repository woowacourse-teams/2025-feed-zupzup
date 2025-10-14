package feedzupzup.backend.feedback.infrastructure.config;

import feedzupzup.backend.feedback.infrastructure.ai.OpenAIProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@EnableConfigurationProperties(OpenAIProperties.class)
@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final OpenAIProperties openAIProperties;

    @Bean
    public RestClient openAiEmbeddingRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(openAIProperties.getConnectTimeout());
        factory.setReadTimeout(openAIProperties.getReadTimeout());

        return RestClient.builder()
                .baseUrl(openAIProperties.getEmbeddingUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAIProperties.getKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(factory)
                .build();
    }
}
