package feedzupzup.backend.feedback.infrastructure.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIErrorResponse {
    private ErrorBody error;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ErrorBody {
        private String message;
        private String type;
        private String param;
        private String code;
    }
}
