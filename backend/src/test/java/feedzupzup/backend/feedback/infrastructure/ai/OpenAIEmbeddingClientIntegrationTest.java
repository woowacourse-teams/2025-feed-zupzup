package feedzupzup.backend.feedback.infrastructure.ai;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.config.ServiceIntegrationHelper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@Disabled
class OpenAIEmbeddingClientIntegrationTest extends ServiceIntegrationHelper {

    @Autowired
    private OpenAIEmbeddingClient openAIEmbeddingClient;

    @Test
    @DisplayName("실제 OpenAI API를 호출하여 임베딩을 생성한다")
    void extractEmbedding_RealAPI_Success() {
        // given
        String inputText = "안녕하세요, 이것은 테스트용 텍스트입니다.";

        // when
        double[] embedding = openAIEmbeddingClient.extractEmbedding(inputText);

        // then
        assertThat(embedding).isNotNull();
        assertThat(embedding.length).isGreaterThan(0);
        assertThat(embedding.length).isEqualTo(1536);
    }

    @Test
    @DisplayName("특수 문자를 포함한 텍스트에 대해 임베딩을 생성한다")
    void extractEmbedding_SpecialCharacters_Success() {
        // given
        String specialText = "특수문자 테스트! @#$%^&*()_+{}[]|:;\"'<>,.?/~`";

        // when
        double[] embedding = openAIEmbeddingClient.extractEmbedding(specialText);

        // then
        assertThat(embedding).isNotNull();
        assertThat(embedding.length).isEqualTo(1536);
    }

    @Test
    @DisplayName("동일한 텍스트는 동일한 임베딩을 생성한다")
    void extractEmbedding_SameText_SameEmbedding() {
        // given
        String text = "동일성 테스트 텍스트";

        // when
        double[] embedding1 = openAIEmbeddingClient.extractEmbedding(text);
        double[] embedding2 = openAIEmbeddingClient.extractEmbedding(text);

        // 각 요소가 충분히 가까운지 확인 (코사인 유사도 활용)
        double cosineSimilarity = calculateCosineSimilarity(embedding1, embedding2);
        assertThat(cosineSimilarity).isGreaterThan(0.9999); // 거의 1에 가까움
    }

    @Test
    @DisplayName("다른 텍스트는 다른 임베딩을 생성한다")
    void extractEmbedding_DifferentText_DifferentEmbedding() {
        // given
        String text1 = "첫 번째 텍스트";
        String text2 = "두 번째 텍스트";

        // when
        double[] embedding1 = openAIEmbeddingClient.extractEmbedding(text1);
        double[] embedding2 = openAIEmbeddingClient.extractEmbedding(text2);

        // then
        assertThat(embedding1).isNotEqualTo(embedding2);
    }

    private double calculateCosineSimilarity(double[] a, double[] b) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
