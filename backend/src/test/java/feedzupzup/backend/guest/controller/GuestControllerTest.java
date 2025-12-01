package feedzupzup.backend.guest.controller;

import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.GUEST_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import feedzupzup.backend.auth.presentation.resolver.GuestArgumentResolver;
import feedzupzup.backend.feedback.dto.response.MyFeedbackListResponse;
import feedzupzup.backend.global.exception.GlobalExceptionHandler;
import feedzupzup.backend.guest.application.GuestService;
import feedzupzup.backend.guest.dto.GuestInfo;
import feedzupzup.backend.guest.dto.response.LikeHistoryResponse;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class GuestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GuestService guestService;

    @InjectMocks
    private GuestController guestController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(guestController)
                .setCustomArgumentResolvers(new GuestArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("좋아요 목록 조회 테스트")
    class LikeHistory {

        @Test
        @DisplayName("본인이 좋아요 누른 피드백 목록을 조회할 수 있어야 한다")
        void get_like_histories() throws Exception {
            // given
            UUID organizationUuid = UUID.randomUUID();
            UUID guestUuid = UUID.randomUUID();
            Long feedbackId = 1L;

            LikeHistoryResponse response = new LikeHistoryResponse(List.of(feedbackId));

            given(guestService.findGuestLikeHistories(eq(organizationUuid), any(GuestInfo.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get("/organizations/{organizationUuid}/feedbacks/my-likes", organizationUuid)
                            .requestAttr(GUEST_ID.getValue(), guestUuid))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.data.feedbackIds[0]").value(feedbackId));

            verify(guestService).findGuestLikeHistories(eq(organizationUuid), any(GuestInfo.class));
        }

        @Test
        @DisplayName("본인이 좋아요 누른 피드백 목록들을 전부 조회할 수 있어야 한다")
        void get_like_histories_multiple_case() throws Exception {
            // given
            UUID organizationUuid = UUID.randomUUID();
            UUID guestUuid = UUID.randomUUID();
            Long feedbackId1 = 1L;
            Long feedbackId2 = 2L;
            Long feedbackId3 = 3L;

            LikeHistoryResponse response = new LikeHistoryResponse(List.of(feedbackId1, feedbackId2, feedbackId3));

            given(guestService.findGuestLikeHistories(eq(organizationUuid), any(GuestInfo.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get("/organizations/{organizationUuid}/feedbacks/my-likes", organizationUuid)
                            .requestAttr(GUEST_ID.getValue(), guestUuid))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.data.feedbackIds").isArray())
                    .andExpect(jsonPath("$.data.feedbackIds.length()").value(3))
                    .andExpect(jsonPath("$.data.feedbackIds[0]").value(feedbackId1))
                    .andExpect(jsonPath("$.data.feedbackIds[1]").value(feedbackId2))
                    .andExpect(jsonPath("$.data.feedbackIds[2]").value(feedbackId3));

            verify(guestService).findGuestLikeHistories(eq(organizationUuid), any(GuestInfo.class));
        }

        @Test
        @DisplayName("좋아요 목록 조회 시, 쿠키가 없다면, 빈 배열이 반환되어야 한다")
        void get_like_histories_none_cookie_case() throws Exception {
            // given
            UUID organizationUuid = UUID.randomUUID();
            UUID guestUuid = UUID.randomUUID();

            LikeHistoryResponse response = new LikeHistoryResponse(Collections.emptyList());

            given(guestService.findGuestLikeHistories(eq(organizationUuid), any(GuestInfo.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get("/organizations/{organizationUuid}/feedbacks/my-likes", organizationUuid)
                            .requestAttr(GUEST_ID.getValue(), guestUuid))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.data.feedbackIds").isEmpty());

            verify(guestService).findGuestLikeHistories(eq(organizationUuid), any(GuestInfo.class));
        }
    }

    @Nested
    @DisplayName("내가 쓴 글 조회 테스트")
    class MyFeedback {

        @Test
        @DisplayName("본인이 작성한 피드백 목록들을 전부 조회할 수 있어야 한다")
        void get_my_feedbacks_multiple_case() throws Exception {
            // given
            UUID organizationUuid = UUID.randomUUID();
            UUID guestUuid = UUID.randomUUID();

            MyFeedbackListResponse response = new MyFeedbackListResponse(Collections.emptyList());

            given(guestService.getMyFeedbackPage(eq(organizationUuid), any(GuestInfo.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get("/organizations/{organizationUuid}/feedbacks/my", organizationUuid)
                            .requestAttr(GUEST_ID.getValue(), guestUuid))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.data.feedbacks").isArray());

            verify(guestService).getMyFeedbackPage(eq(organizationUuid), any(GuestInfo.class));
        }

        @Test
        @DisplayName("내가 쓴 글 조회 시, 쿠키가 없다면, 빈 배열이 반환되어야 한다")
        void get_my_feedbacks_none_cookie_case() throws Exception {
            // given
            UUID organizationUuid = UUID.randomUUID();
            UUID guestUuid = UUID.randomUUID();

            MyFeedbackListResponse response = new MyFeedbackListResponse(Collections.emptyList());

            given(guestService.getMyFeedbackPage(eq(organizationUuid), any(GuestInfo.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get("/organizations/{organizationUuid}/feedbacks/my", organizationUuid)
                            .requestAttr(GUEST_ID.getValue(), guestUuid))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.data.feedbacks").isEmpty());

            verify(guestService).getMyFeedbackPage(eq(organizationUuid), any(GuestInfo.class));
        }
    }
}