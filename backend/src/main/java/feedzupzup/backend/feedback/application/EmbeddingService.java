package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.EmbeddingExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final EmbeddingExtractor embeddingExtractor;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public double[] extractEmbedding(final String content) {
        return embeddingExtractor.extract(content);
    }

}
