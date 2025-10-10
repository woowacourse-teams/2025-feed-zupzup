package feedzupzup.backend.feedback.infrastructure;

import feedzupzup.backend.feedback.domain.EmbeddingExtractor;
import feedzupzup.backend.feedback.infrastructure.ai.OpenAIEmbeddingClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenAIEmbeddingExtractorAdapter implements EmbeddingExtractor {

    private final OpenAIEmbeddingClient openAIEmbeddingClient;

    @Override
    public double[] extract(final String text) {
        return openAIEmbeddingClient.extractEmbedding(text);
    }
}
