package feedzupzup.backend.feedback.infrastructure.embedding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoyageAIErrorResponse {
    private String status;
    private String error;
    private String message;
}
