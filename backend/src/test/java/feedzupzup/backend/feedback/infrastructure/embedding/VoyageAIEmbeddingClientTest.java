package feedzupzup.backend.feedback.infrastructure.embedding;

import static feedzupzup.backend.feedback.infrastructure.embedding.VoyageAIEmbeddingResponse.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import feedzupzup.backend.global.async.exception.RetryableException;
import feedzupzup.backend.global.exception.InfrastructureException.RestClientServerException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class VoyageAIEmbeddingClientTest {

    @Mock
    private VoyageAIProperties voyageAIProperties;

    @Mock
    private RestClient voyageAiEmbeddingRestClient;

    @Mock
    private VoyageAIErrorHandler voyageAIErrorHandler;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private VoyageAIEmbeddingClient voyageAIEmbeddingClient;

    @Test
    @DisplayName("정상적으로 임베딩을 생성한다")
    void extractEmbedding_Success() {
        // given
        String inputText = "테스트 입력 텍스트";
        String model = "voyage-3-large";
        double[] expectedEmbedding = {0.1, 0.2, 0.3, 0.4, 0.5};

        EmbeddingData embeddingData = new EmbeddingData();
        embeddingData.setEmbedding(expectedEmbedding);

        Usage usage = new Usage();
        usage.setTotalTokens(100);

        VoyageAIEmbeddingResponse response = new VoyageAIEmbeddingResponse();
        response.setData(List.of(embeddingData));
        response.setUsage(usage);

        given(voyageAIProperties.getEmbeddingModel()).willReturn(model);
        given(voyageAiEmbeddingRestClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.body(any(Map.class))).willReturn(requestBodySpec);
        given(requestBodySpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.onStatus(any(), any())).willReturn(responseSpec);
        given(responseSpec.body(VoyageAIEmbeddingResponse.class)).willReturn(response);

        // when
        double[] result = voyageAIEmbeddingClient.extractEmbedding(inputText);

        // then
        assertThat(result).isEqualTo(expectedEmbedding);
        verify(requestBodyUriSpec).body(eq(Map.of(
                "input", inputText,
                "model", model,
                "input_type", "document"
        )));
    }

    @Test
    @DisplayName("빈 응답 데이터에 대해 예외를 발생시킨다")
    void extractEmbedding_EmptyResponse_ThrowsException() {
        // given
        String inputText = "테스트 입력 텍스트";
        String model = "voyage-3-large";

        VoyageAIEmbeddingResponse response = new VoyageAIEmbeddingResponse();
        response.setData(List.of());

        given(voyageAIProperties.getEmbeddingModel()).willReturn(model);
        given(voyageAiEmbeddingRestClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.body(any(Map.class))).willReturn(requestBodySpec);
        given(requestBodySpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.onStatus(any(), any())).willReturn(responseSpec);
        given(responseSpec.body(VoyageAIEmbeddingResponse.class)).willReturn(response);

        // when & then
        assertThatThrownBy(() -> voyageAIEmbeddingClient.extractEmbedding(inputText))
                .isInstanceOf(RetryableException.class)
                .hasMessageContaining("임베딩 데이터가 비어 있거나 응답이 없습니다");
    }

    @Test
    @DisplayName("null 응답에 대해 예외를 발생시킨다")
    void extractEmbedding_NullResponse_ThrowsException() {
        // given
        String inputText = "테스트 입력 텍스트";
        String model = "voyage-3-large";

        given(voyageAIProperties.getEmbeddingModel()).willReturn(model);
        given(voyageAiEmbeddingRestClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.body(any(Map.class))).willReturn(requestBodySpec);
        given(requestBodySpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.onStatus(any(), any())).willReturn(responseSpec);
        given(responseSpec.body(VoyageAIEmbeddingResponse.class)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> voyageAIEmbeddingClient.extractEmbedding(inputText))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("임베딩 데이터가 비어 있거나 응답이 없습니다");
    }
}
