package feedzupzup.backend.feedback.infrastructure.ai;

import static feedzupzup.backend.feedback.infrastructure.ai.OpenAIEmbeddingResponse.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import feedzupzup.backend.global.exception.InfrastructureException.RestClientServerException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class OpenAIEmbeddingClientTest {

    @Mock
    private OpenAIProperties openAIProperties;

    @Mock
    private RestClient openAiEmbeddingRestClient;

    @Mock
    private OpenAIErrorHandler openAIErrorHandler;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private OpenAIEmbeddingClient openAIEmbeddingClient;

    @Test
    @DisplayName("정상적으로 임베딩을 생성한다")
    void extractEmbedding_Success() {
        // given
        String inputText = "테스트 입력 텍스트";
        String model = "text-embedding-ada-002";
        double[] expectedEmbedding = {0.1, 0.2, 0.3, 0.4, 0.5};

        EmbeddingData embeddingData = new EmbeddingData();
        embeddingData.setEmbedding(expectedEmbedding);

        OpenAIEmbeddingResponse response = new OpenAIEmbeddingResponse();
        response.setData(List.of(embeddingData));

        given(openAIProperties.getEmbeddingModel()).willReturn(model);
        given(openAiEmbeddingRestClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.body(any(Map.class))).willReturn(requestBodySpec);
        given(requestBodySpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.onStatus(any(), any())).willReturn(responseSpec);
        given(responseSpec.body(OpenAIEmbeddingResponse.class)).willReturn(response);

        // when
        double[] result = openAIEmbeddingClient.extractEmbedding(inputText);

        // then
        assertThat(result).isEqualTo(expectedEmbedding);
        verify(requestBodyUriSpec).body(eq(Map.of(
                "input", inputText,
                "model", model
        )));
    }

    @Test
    @DisplayName("빈 응답 데이터에 대해 예외를 발생시킨다")
    void extractEmbedding_EmptyResponse_ThrowsException() {
        // given
        String inputText = "테스트 입력 텍스트";
        String model = "text-embedding-ada-002";

        OpenAIEmbeddingResponse response = new OpenAIEmbeddingResponse();
        response.setData(List.of());

        given(openAIProperties.getEmbeddingModel()).willReturn(model);
        given(openAiEmbeddingRestClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.body(any(Map.class))).willReturn(requestBodySpec);
        given(requestBodySpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.onStatus(any(), any())).willReturn(responseSpec);
        given(responseSpec.body(OpenAIEmbeddingResponse.class)).willReturn(response);

        // when & then
        assertThatThrownBy(() -> openAIEmbeddingClient.extractEmbedding(inputText))
                .isInstanceOf(RestClientServerException.class)
                .hasMessageContaining("임베딩 데이터가 비어 있거나 응답이 없습니다");
    }

    @Test
    @DisplayName("null 응답에 대해 예외를 발생시킨다")
    void extractEmbedding_NullResponse_ThrowsException() {
        // given
        String inputText = "테스트 입력 텍스트";
        String model = "text-embedding-ada-002";

        given(openAIProperties.getEmbeddingModel()).willReturn(model);
        given(openAiEmbeddingRestClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.body(any(Map.class))).willReturn(requestBodySpec);
        given(requestBodySpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.onStatus(any(), any())).willReturn(responseSpec);
        given(responseSpec.body(OpenAIEmbeddingResponse.class)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> openAIEmbeddingClient.extractEmbedding(inputText))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("임베딩 데이터가 비어 있거나 응답이 없습니다");
    }

    @Test
    @DisplayName("RestClient 예외 발생 시 RestClientServerException으로 변환한다")
    void extractEmbedding_RestClientException_ThrowsRestClientServerException() {
        // given
        String inputText = "테스트 입력 텍스트";
        String model = "text-embedding-ada-002";

        given(openAIProperties.getEmbeddingModel()).willReturn(model);
        given(openAiEmbeddingRestClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.body(any(Map.class))).willReturn(requestBodySpec);
        given(requestBodySpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.onStatus(any(), any())).willReturn(responseSpec);
        given(responseSpec.body(OpenAIEmbeddingResponse.class))
                .willThrow(new RestClientException("네트워크 오류"));

        // when & then
        assertThatThrownBy(() -> openAIEmbeddingClient.extractEmbedding(inputText))
                .isInstanceOf(RestClientServerException.class)
                .hasMessageContaining("임베딩 생성 중 오류 발생");
    }
}
