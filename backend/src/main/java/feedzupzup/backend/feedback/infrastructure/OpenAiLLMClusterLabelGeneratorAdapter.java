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
            여러 개의 피드백을 읽고, 핵심 요구사항 또는 문제를
            짧고 명확하게 한 문장으로 요약하는 역할을 맡고 있습니다.
            
            다음 원칙을 반드시 지킵니다:
            - 피드백의 첫 번째 문장에만 끌리지 말고, 전체 내용을 종합해 공통된 핵심을 표현합니다.
            - 세부적인 사례나 특정 상황에 치우치지 않고, 한 단계 추상화된 핵심 요구로 표현합니다.
            - 브랜드명, 인물명, 고유명사 등은 포함하지 않습니다.
            - 주관적인 표현(많습니다, 다양합니다 등)은 사용하지 않습니다.
            - 문장 끝은 반드시 존댓말로 마무리합니다.
            - 불필요한 배경 설명 없이 요약문만 출력합니다.
            """;

    private static final String CLUSTER_LABEL_PROMPT_TEMPLATE = """
            다음은 같은 주제로 묶인 고객 피드백들입니다.
            이 피드백들의 공통된 요구사항 또는 문제를
            지나치게 구체적이지 않게, 한 단계 추상화하여 한 문장으로 요약해주세요.
            
            요구사항:
            1. 반드시 한국어로 작성
            2. 한 문장으로 작성 (20~30자 권장)
            3. 구체적인 사례 대신 공통 요구나 문제로 요약
            4. 문장 끝은 반드시 존댓말로 마무리
            5. 요약문만 출력 (설명, 부연, 해석 금지)
            6. '많습니다', '다양합니다' 등의 주관적 표현 금지
            7. 브랜드명, 인물명 등 고유명사 포함 금지
            8. 첫 번째 피드백에 치우치지 말고 전체를 종합하여 요약
            
            좋은 예시:
            - 피드백: "로그인이 자주 풀려서 다시 로그인해야 한다", "자동 로그인이 안 된다", "앱을 껐다 켜면 로그아웃됨"
              → 요약: "자동 로그인 기능이 정상적으로 유지되지 않습니다."
            
            - 피드백: "이벤트 알림을 늦게 받는다", "알림이 오지 않아서 참여를 놓쳤다", "알림이 제때 오지 않는다"
              → 요약: "이벤트 알림이 제때 전송되지 않습니다."
            
            - 피드백: "검색 결과가 부정확하다", "원하는 내용을 찾기 어렵다", "검색 결과가 엉뚱하게 나온다"
              → 요약: "검색 결과가 기대와 다르게 표시됩니다."
            
            - 피드백: "버튼이 작아서 누르기 힘들다", "모바일에서 클릭이 잘 안 된다", "UI가 너무 작다"
              → 요약: "모바일에서 버튼 클릭이 어렵습니다."
            
            - 피드백: "공지사항을 다시 찾기 어렵다", "중요한 정보가 금방 사라진다", "정보 접근이 어렵다"
              → 요약: "중요한 공지 정보를 다시 확인하기 어렵습니다."
            
            나쁜 예시 (하지 말 것) ↔ 좋은 예시 (이렇게 바꾸세요):
            - ❌ "캉골이 제대로 작동을 안 한다." → ✅ "특정 기능이 정상적으로 작동하지 않습니다."
            - ❌ "첫 번째 피드백에서 말한 로그인이 자주 풀린다." → ✅ "자동 로그인 기능이 정상적으로 유지되지 않습니다."
            - ❌ "로그인이 자주 풀리는 경우가 많습니다." → ✅ "자동 로그인 기능이 정상적으로 유지되지 않습니다."
            - ❌ "로그인 문제에 대해 고객들이 불만을 토로하고 있습니다." → ✅ "로그인 기능이 정상적으로 동작하지 않습니다."
            - ❌ "로그인 풀림" → ✅ "로그인 상태가 지속적으로 유지되지 않습니다."
            - ❌ "서비스가 이상함." → ✅ "서비스 이용 과정에서 오류가 발생합니다."
            
            **부정적인 말은 부정적으로, 긍정적인 말은 긍정적으로 표현해줘!**
            **맥락을 해석해서, 현재 어떤 상태인지 말해줘!**
            
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
