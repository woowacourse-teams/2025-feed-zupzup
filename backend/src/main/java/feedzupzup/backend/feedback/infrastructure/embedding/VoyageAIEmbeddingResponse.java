package feedzupzup.backend.feedback.infrastructure.embedding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoyageAIEmbeddingResponse {
    @JsonProperty("data")
    private List<EmbeddingData> data;

    @JsonProperty("usage")
    private Usage usage;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EmbeddingData {
        private double[] embedding;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
}
