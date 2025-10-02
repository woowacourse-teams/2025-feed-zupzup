package feedzupzup.backend.feedback.infrastructure.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import feedzupzup.backend.feedback.infrastructure.ai.OpenAIErrorResponse.ErrorBody;
import feedzupzup.backend.global.exception.InfrastructureException.RestClientServerException;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OpenAIErrorHandler {

    private final ObjectMapper objectMapper;

    public void handleError(HttpRequest request, ClientHttpResponse response) {
        try (InputStream bodyStream = response.getBody()) {
            OpenAIErrorResponse errorResponse = objectMapper.readValue(bodyStream, OpenAIErrorResponse.class);
            ErrorBody errorBody = errorResponse.getError();

            log.error("OpenAI API 실패 [{}:{}] {}", errorBody.getType(), errorBody.getCode(), errorBody.getMessage());
            throw new RestClientServerException("OpenAI API를 실패하였습니다. request URI : " + request.getURI());
        } catch (IOException e) {
            log.error("OpenAI Error Parsing Failed: {}", e.getMessage());
            throw new RestClientServerException("OpenAI 에러 응답 파싱 실패", e);
        }
    }
}
