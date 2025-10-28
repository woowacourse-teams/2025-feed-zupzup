package feedzupzup.backend.feedback.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.ClusterLabelGenerator;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@Disabled
class OpenAiLLMClusterLabelGeneratorAdapterIntegrationTest extends ServiceIntegrationHelper {

    @Autowired
    private ClusterLabelGenerator clusterLabelGenerator;

    @Test
    @DisplayName("실제 OpenAI API 호출로 클러스터 라벨을 생성한다")
    void generate_WithRealAPICall() {
        // given
        final List<String> feedbackContents = List.of(
                "앱이 너무 느려요. 속도 개선이 필요합니다.",
                "로딩 시간이 너무 오래 걸려서 불편해요",
                "앱 실행 속도가 느린 것 같아요"
        );

        // when
        String result = clusterLabelGenerator.generate(feedbackContents);

        System.out.println(result);
        // then
        assertThat(result)
                .isNotNull()
                .isNotBlank()
                .hasSizeLessThanOrEqualTo(100);
    }

    @Test
    @DisplayName("단일 피드백으로 라벨을 생성한다")
    void generate_WithSingleFeedback() {
        // given
        final List<String> feedbackContents = List.of("결제 시스템에 오류가 발생했습니다");

        // when
        String result = clusterLabelGenerator.generate(feedbackContents);

        // then
        assertThat(result)
                .isNotNull()
                .isNotBlank()
                .hasSizeLessThanOrEqualTo(100);
    }
}
