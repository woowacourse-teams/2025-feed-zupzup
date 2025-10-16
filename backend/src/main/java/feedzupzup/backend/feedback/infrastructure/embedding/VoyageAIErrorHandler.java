package feedzupzup.backend.feedback.infrastructure.embedding;

import com.fasterxml.jackson.databind.ObjectMapper;
import feedzupzup.backend.global.exception.InfrastructureException.RestClientServerException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class VoyageAIErrorHandler {

    private final ObjectMapper objectMapper;

    public void handleError(final HttpRequest request, final ClientHttpResponse response) {
        try (InputStream bodyStream = response.getBody()) {
            final VoyageAIErrorResponse errorResponse = objectMapper.readValue(bodyStream, VoyageAIErrorResponse.class);

            log.error("VoyageAI API 실패 내용 : {}", errorResponse.getDetail());
            throw new RestClientServerException("VoyageAI API를 실패하였습니다. request URI : " + request.getURI());
        } catch (IOException e) {
            log.error("VoyageAI Error Parsing Failed: {}", e.getMessage());
            throw new RestClientServerException("VoyageAI 에러 응답 파싱 실패", e);
        }
    }
}
