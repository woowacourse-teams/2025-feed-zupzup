package feedzupzup.backend.feedback.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserFeedbackServiceTest extends ServiceIntegrationHelper{

    @Autowired
    private UserFeedbackService userFeedbackService;

    @Nested
    @DisplayName("피드백 생성 테스트")
    class CreateFeedbackTest {

        @Test
        @DisplayName("피드백을 성공적으로 생성한다")
        void create_success() {
            //given
            final Long placeId = 1L;
            final CreateFeedbackRequest request = new CreateFeedbackRequest("윌슨", "맛있어요", false);

            //when
            final CreateFeedbackResponse response = userFeedbackService.create(request, placeId);

            //then
            assertAll(
                    () -> assertThat(response.feedbackId()).isNotNull(),
                    () -> assertThat(response.content()).isEqualTo(request.content()),
                    () -> assertThat(response.imageUrl()).isEqualTo(request.imageUrl()),
                    () -> assertThat(response.isSecret()).isEqualTo(request.isSecret()),
                    () -> assertThat(response.createdAt()).isNotNull()
            );
        }
    }
}
