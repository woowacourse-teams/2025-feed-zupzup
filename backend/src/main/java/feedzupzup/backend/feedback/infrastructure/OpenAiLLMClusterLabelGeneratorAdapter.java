package feedzupzup.backend.feedback.infrastructure;

import feedzupzup.backend.feedback.domain.ClusterLabelGenerator;
import feedzupzup.backend.feedback.exception.ClusterException.EmptyClusteringContentException;
import feedzupzup.backend.feedback.infrastructure.llm.OpenAICompletionClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenAiLLMClusterLabelGeneratorAdapter implements ClusterLabelGenerator {

    private static final String SYSTEM_MESSAGE = """
            당신은 고객 피드백을 분석하는 전문가입니다.
            여러 개의 고객 피드백을 읽고, 핵심 내용을 한 문장으로 요약합니다.
            고객들이 무엇을 불편해하는지, 무엇을 요구하는지 명확하게 표현합니다.
            """;

    private static final String CLUSTER_LABEL_PROMPT_TEMPLATE = """
            다음은 같은 주제로 묶인 고객 피드백들입니다.
            이 피드백들의 공통된 내용을 파악하여 한 문장으로 요약해주세요.
            
            요구사항:
            1. 반드시 한국어로 작성
            2. 한 문장으로 작성 (20-30자 권장)
            3. 구체적이고 명확한 표현 사용
            4. 존댓말을 사용한다
            5. 요약문만 출력하고 다른 설명이나 부연은 절대 포함하지 마세요
            6. ~많습니다 등 주관적인 이야기를 하지 마세요. 팩트만 전달하세요.
            7. 특정 인물 이름을 포함시키지 않고, 주로 행위로만 이야기 해주세요
            
            피드백 내용:
            %s
            
            요약:
            """;
    private final OpenAICompletionClient openAICompletionClient;

    @Override
    public String generate(final List<String> feedbackContents) {
        if (feedbackContents == null || feedbackContents.isEmpty()) {
            log.warn("클러스터 내용이 비어있습니다.");
            throw new EmptyClusteringContentException();
        }

        final String combinedContents = combineContents(feedbackContents);
        final String prompt = String.format(CLUSTER_LABEL_PROMPT_TEMPLATE, combinedContents);
        log.info("클러스터 라벨 생성 시작 - 피드백 개수: {}, 총 길이: {}", feedbackContents.size(), combinedContents.length());

        return openAICompletionClient.generateCompletion(prompt, SYSTEM_MESSAGE);
    }

    private String combineContents(final List<String> clusterContents) {
        final StringBuilder combined = new StringBuilder();

        for (int i = 0; i < clusterContents.size(); i++) {
            combined.append(String.format("%d. %s\n", i + 1, clusterContents.get(i).trim()));
        }

        return combined.toString().trim();
    }
}
