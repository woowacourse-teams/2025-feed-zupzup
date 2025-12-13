package feedzupzup.backend.lambda.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feedzupzup.backend.lambda.config.LambdaProperties;
import feedzupzup.backend.lambda.dto.FeedbackExcelLambdaRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackExcelLambdaService {

    private final LambdaClient lambdaClient;
    private final LambdaProperties lambdaProperties;
    private final ObjectMapper objectMapper;

    public void invokeFeedbackExcelGeneration(final FeedbackExcelLambdaRequest request) {
        try {
            final String payload = objectMapper.writeValueAsString(request);

            final InvokeRequest invokeRequest = InvokeRequest.builder()
                    .functionName(lambdaProperties.feedbackExcelFunctionName())
                    .invocationType(InvocationType.EVENT)
                    .payload(SdkBytes.fromUtf8String(payload))
                    .build();

            lambdaClient.invoke(invokeRequest);

            log.info("람다 호출 성공 jobId: {}", request.jobId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lambda 요청 직렬화 실패", e);
        } catch (Exception e) {
            throw new RuntimeException("Lambda 호출 실패", e);
        }
    }
}
