package feedzupzup.backend.feedback.domain;

@FunctionalInterface
public interface EmbeddingExtractor {

    double[] extract(final String text);
}
