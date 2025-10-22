package feedzupzup.backend.feedback.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import feedzupzup.backend.feedback.exception.ClusterException.EmptyClusteringContentException;
import feedzupzup.backend.feedback.infrastructure.ai.OpenAICompletionClient;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpenAiLLMClusterLabelGeneratorAdapterTest {

    @Mock
    private OpenAICompletionClient openAICompletionClient;

    @InjectMocks
    private OpenAiLLMClusterLabelGeneratorAdapter openAiLLMClusterLabelGeneratorAdapter;

    @Test
    @DisplayName("피드백 내용 리스트로부터 클러스터 라벨을 생성한다")
    void generate_ReturnLabel_WithFeedbackContents() {
        // given
        final List<String> feedbackContents = List.of(
                "앱이 너무 느려요",
                "로딩 시간이 오래 걸립니다",
                "앱 속도를 개선해주세요"
        );
        final String expectedLabel = "고객들이 앱 속도 개선을 요구한다";
        given(openAICompletionClient.generateCompletion(any(String.class), any(String.class)))
                .willReturn(expectedLabel);

        // when
        String result = openAiLLMClusterLabelGeneratorAdapter.generate(feedbackContents);

        // then
        assertThat(result).isEqualTo(expectedLabel);
        verify(openAICompletionClient).generateCompletion(any(String.class), any(String.class));
    }

    @Test
    @DisplayName("단일 피드백으로부터 클러스터 라벨을 생성한다")
    void generate_ReturnLabel_WithSingleFeedback() {
        // given
        final List<String> feedbackContents = List.of("결제 시스템에 오류가 있어요");
        final String expectedLabel = "고객들이 결제 시스템 오류 수정을 요구한다";
        given(openAICompletionClient.generateCompletion(any(String.class), any(String.class)))
                .willReturn(expectedLabel);

        // when
        String result = openAiLLMClusterLabelGeneratorAdapter.generate(feedbackContents);

        // then
        assertThat(result).isEqualTo(expectedLabel);
        verify(openAICompletionClient).generateCompletion(any(String.class), any(String.class));
    }

    @Test
    @DisplayName("피드백 내용이 null이면 예외를 발생시킨다")
    void generate_ThrowException_WhenFeedbackContentsIsNull() {
        // given
        final List<String> feedbackContents = null;

        // when & then
        assertThatThrownBy(() -> openAiLLMClusterLabelGeneratorAdapter.generate(feedbackContents))
                .isInstanceOf(EmptyClusteringContentException.class);
    }

    @Test
    @DisplayName("피드백 내용이 빈 리스트이면 예외를 발생시킨다")
    void generate_ThrowException_WhenFeedbackContentsIsEmpty() {
        // given
        final List<String> feedbackContents = Collections.emptyList();

        // when & then
        assertThatThrownBy(() -> openAiLLMClusterLabelGeneratorAdapter.generate(feedbackContents))
                .isInstanceOf(EmptyClusteringContentException.class);
    }
}
