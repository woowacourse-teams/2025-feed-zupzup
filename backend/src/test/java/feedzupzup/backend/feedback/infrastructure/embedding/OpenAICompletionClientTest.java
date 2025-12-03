package feedzupzup.backend.feedback.infrastructure.embedding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import feedzupzup.backend.feedback.infrastructure.llm.OpenAICompletionClient;
import feedzupzup.backend.feedback.infrastructure.llm.OpenAICompletionResponse;
import feedzupzup.backend.feedback.infrastructure.llm.OpenAIErrorHandler;
import feedzupzup.backend.feedback.infrastructure.llm.OpenAIProperties;
import feedzupzup.backend.global.async.exception.RetryableException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class OpenAICompletionClientTest {

    @Mock
    private OpenAIProperties openAIProperties;

    @Mock
    private RestClient openAiCompletionRestClient;

    @Mock
    private OpenAIErrorHandler openAIErrorHandler;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private OpenAICompletionClient openAICompletionClient;

    @BeforeEach
    void setUp() {
        openAICompletionClient = new OpenAICompletionClient(
                openAIProperties, openAiCompletionRestClient, openAIErrorHandler);
    }

    @Test
    @DisplayName("정상적인 요청으로 텍스트 생성 성공")
    void generateCompletion_Success() {
        // given
        final String prompt = "테스트 프롬프트";
        final String systemMessage = "시스템 메시지";
        final String expectedCompletion = "생성된 텍스트";

        given(openAIProperties.getCompletionModel()).willReturn("gpt-3.5-turbo");
        given(openAIProperties.getMaxTokens()).willReturn(1000);
        given(openAIProperties.getTemperature()).willReturn(0.7);

        OpenAICompletionResponse.Usage usage = new OpenAICompletionResponse.Usage();
        usage.setTotalTokens(100);

        OpenAICompletionResponse.Message message = new OpenAICompletionResponse.Message();
        message.setContent(expectedCompletion);

        OpenAICompletionResponse.Choice choice = new OpenAICompletionResponse.Choice();
        choice.setMessage(message);

        OpenAICompletionResponse response = new OpenAICompletionResponse();
        response.setChoices(List.of(choice));
        response.setUsage(usage);

        when(openAiCompletionRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Map.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(OpenAICompletionResponse.class)).thenReturn(response);

        // when
        String result = openAICompletionClient.generateCompletion(prompt, systemMessage);

        // then
        assertThat(result).isEqualTo(expectedCompletion);
        verify(openAiCompletionRestClient).post();
    }

    @Test
    @DisplayName("응답이 null이면 예외 발생")
    void generateCompletion_ThrowException_WhenResponseIsNull() {
        // given
        final String prompt = "테스트 프롬프트";
        final String systemMessage = "시스템 메시지";

        given(openAIProperties.getCompletionModel()).willReturn("gpt-3.5-turbo");
        given(openAIProperties.getMaxTokens()).willReturn(1000);
        given(openAIProperties.getTemperature()).willReturn(0.7);

        when(openAiCompletionRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Map.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(OpenAICompletionResponse.class)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> openAICompletionClient.generateCompletion(prompt, systemMessage))
                .isInstanceOf(RetryableException.class)
                .hasMessageContaining("생성된 텍스트가 비어 있거나 응답이 없습니다");
    }

    @Test
    @DisplayName("응답의 choices가 비어있으면 예외 발생")
    void generateCompletion_ThrowException_WhenChoicesIsEmpty() {
        // given
        final String prompt = "테스트 프롬프트";
        final String systemMessage = "시스템 메시지";

        given(openAIProperties.getCompletionModel()).willReturn("gpt-3.5-turbo");
        given(openAIProperties.getMaxTokens()).willReturn(1000);
        given(openAIProperties.getTemperature()).willReturn(0.7);

        OpenAICompletionResponse response = new OpenAICompletionResponse();
        response.setChoices(List.of());

        when(openAiCompletionRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Map.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(OpenAICompletionResponse.class)).thenReturn(response);

        // when & then
        assertThatThrownBy(() -> openAICompletionClient.generateCompletion(prompt, systemMessage))
                .isInstanceOf(RetryableException.class)
                .hasMessageContaining("생성된 텍스트가 비어 있거나 응답이 없습니다");
    }

    @Test
    @DisplayName("생성된 내용이 빈 문자열이면 예외 발생")
    void generateCompletion_ThrowException_WhenContentIsEmpty() {
        // given
        final String prompt = "테스트 프롬프트";
        final String systemMessage = "시스템 메시지";

        given(openAIProperties.getCompletionModel()).willReturn("gpt-3.5-turbo");
        given(openAIProperties.getMaxTokens()).willReturn(1000);
        given(openAIProperties.getTemperature()).willReturn(0.7);

        OpenAICompletionResponse.Message message = new OpenAICompletionResponse.Message();
        message.setContent("   ");

        OpenAICompletionResponse.Choice choice = new OpenAICompletionResponse.Choice();
        choice.setMessage(message);

        OpenAICompletionResponse response = new OpenAICompletionResponse();
        response.setChoices(List.of(choice));

        when(openAiCompletionRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Map.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(OpenAICompletionResponse.class)).thenReturn(response);

        // when & then
        assertThatThrownBy(() -> openAICompletionClient.generateCompletion(prompt, systemMessage))
                .isInstanceOf(RetryableException.class)
                .hasMessageContaining("생성된 텍스트가 비어 있거나 응답이 없습니다");
    }
}
