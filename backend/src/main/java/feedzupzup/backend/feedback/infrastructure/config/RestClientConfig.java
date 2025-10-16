package feedzupzup.backend.feedback.infrastructure.config;

import feedzupzup.backend.feedback.infrastructure.embedding.VoyageAIProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@EnableConfigurationProperties(VoyageAIProperties.class)
@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final VoyageAIProperties voyageAIProperties;

    @Bean
    public RestClient voyageAiEmbeddingRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(voyageAIProperties.getConnectTimeout());
        factory.setReadTimeout(voyageAIProperties.getReadTimeout());

        return RestClient.builder()
                .baseUrl(voyageAIProperties.getEmbeddingUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + voyageAIProperties.getKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(factory)
                .build();
    }
}
