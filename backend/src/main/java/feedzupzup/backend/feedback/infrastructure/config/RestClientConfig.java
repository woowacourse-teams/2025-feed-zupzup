package feedzupzup.backend.feedback.infrastructure.config;

import feedzupzup.backend.feedback.infrastructure.llm.OpenAIProperties;
import feedzupzup.backend.feedback.infrastructure.embedding.VoyageAIProperties;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@EnableConfigurationProperties({VoyageAIProperties.class, OpenAIProperties.class})
@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final VoyageAIProperties voyageAIProperties;
    private final OpenAIProperties openAIProperties;

    @Bean
    public RestClient voyageAiEmbeddingRestClient() {
        CloseableHttpClient httpClient = createHttpClient(
                voyageAIProperties.getConnectTimeout(),
                voyageAIProperties.getReadTimeout()
        );
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return RestClient.builder()
                .baseUrl(voyageAIProperties.getEmbeddingUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + voyageAIProperties.getKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(factory)
                .build();
    }

    @Bean
    public RestClient openAiCompletionRestClient() {
        CloseableHttpClient httpClient = createHttpClient(
                openAIProperties.getConnectTimeout(),
                openAIProperties.getReadTimeout()
        );
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return RestClient.builder()
                .baseUrl(openAIProperties.getCompletionUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAIProperties.getKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(factory)
                .build();
    }

    private CloseableHttpClient createHttpClient(Duration connectTimeout, Duration readTimeout) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultConnectionConfig(
                ConnectionConfig.custom()
                        .setConnectTimeout(Timeout.of(connectTimeout))
                        .setSocketTimeout(Timeout.of(readTimeout))
                        .build()
        );

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.of(connectTimeout))
                .build();

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }
}
