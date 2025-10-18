package feedzupzup.backend.feedback.infrastructure;

import feedzupzup.backend.feedback.domain.EmbeddingExtractor;
import feedzupzup.backend.feedback.infrastructure.embedding.VoyageAIEmbeddingClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@RequiredArgsConstructor
public class VoyageAIEmbeddingExtractorAdapter implements EmbeddingExtractor {

    private final VoyageAIEmbeddingClient voyageAIEmbeddingClient;

    @Override
    public double[] extract(final String text) {
        return voyageAIEmbeddingClient.extractEmbedding(text);
    }
}
