package feedzupzup.backend.feedback.controller;

import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.GUEST_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import feedzupzup.backend.auth.presentation.resolver.GuestArgumentResolver;
import feedzupzup.backend.feedback.application.UserFeedbackService;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortType;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackItem;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.global.exception.GlobalExceptionHandler;
import feedzupzup.backend.guest.dto.GuestInfo;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserFeedbackControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private UserFeedbackService userFeedbackService;

    @InjectMocks
    private UserFeedbackController userFeedbackController;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(userFeedbackController)
                .setCustomArgumentResolvers(new GuestArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("사용자가 특정 장소의 피드백 목록을 성공적으로 조회한다")
    void user_get_feedbacks_success() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        UUID guestUuid = UUID.randomUUID();

        UserFeedbackListResponse response = new UserFeedbackListResponse(
                List.of(
                        createUserFeedbackItem(3L, "피드백3", null),
                        createUserFeedbackItem(2L, "피드백2", null),
                        createUserFeedbackItem(1L, "피드백1", null)
                ),
                false,
                1L
        );

        given(userFeedbackService.getFeedbackPage(
                eq(organizationUuid),
                eq(10),
                isNull(),
                any(),
                eq(FeedbackSortType.LATEST),
                eq(new GuestInfo(guestUuid))
                )
        ).willReturn(response);

        // when & then
        mockMvc.perform(get("/organizations/{organizationUuid}/feedbacks", organizationUuid)
                        .requestAttr(GUEST_ID.getValue(), guestUuid)
                        .param("size", "10")
                        .param("sortBy", "LATEST"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.feedbacks.length()").value(3))
                .andExpect(jsonPath("$.data.hasNext").value(false));
    }

    @Test
    @DisplayName("사용자가 커서 기반 페이징으로 피드백 목록을 조회한다")
    void user_get_feedbacks_with_cursor_pagination() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        UUID guestUuid = UUID.randomUUID();

        UserFeedbackListResponse firstPageResponse = new UserFeedbackListResponse(
                List.of(
                        createUserFeedbackItem(3L, "피드백3", null),
                        createUserFeedbackItem(2L, "피드백2", null)
                ),
                true, 2L
        );

        given(userFeedbackService.getFeedbackPage(
                eq(organizationUuid),
                eq(2),
                isNull(),
                any(),
                eq(FeedbackSortType.LATEST),
                any() // GuestInfo
        )).willReturn(firstPageResponse);

        UserFeedbackListResponse secondPageResponse = new UserFeedbackListResponse(
                List.of(createUserFeedbackItem(1L, "피드백1", null)),
                false, 1L
        );

        given(userFeedbackService.getFeedbackPage(
                eq(organizationUuid),
                eq(2),
                eq(2L),
                any(),
                eq(FeedbackSortType.LATEST),
                any()
        )).willReturn(secondPageResponse);


        // when & then - 첫 번째 페이지
        mockMvc.perform(get("/organizations/{organizationUuid}/feedbacks", organizationUuid)
                        .requestAttr(GUEST_ID.getValue(), guestUuid)
                        .param("size", "2")
                        .param("sortBy", "LATEST"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.feedbacks.length()").value(2))
                .andExpect(jsonPath("$.data.hasNext").value(true))
                .andExpect(jsonPath("$.data.nextCursorId").value(2));

        // when & then - 두 번째 페이지
        mockMvc.perform(get("/organizations/{organizationUuid}/feedbacks", organizationUuid)
                        .requestAttr(GUEST_ID.getValue(), guestUuid)
                        .param("size", "2")
                        .param("cursorId", "2")
                        .param("sortBy", "LATEST"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.feedbacks.length()").value(1))
                .andExpect(jsonPath("$.data.hasNext").value(false));
    }

    @Test
    @DisplayName("사용자가 빈 피드백 목록을 조회한다")
    void user_get_empty_feedbacks() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        UUID guestUuid = UUID.randomUUID();

        UserFeedbackListResponse emptyResponse = new UserFeedbackListResponse(
                Collections.emptyList(), false, null
        );

        given(userFeedbackService.getFeedbackPage(
                eq(organizationUuid),
                eq(10),
                isNull(),
                any(),
                eq(FeedbackSortType.LATEST),
                any()
        )).willReturn(emptyResponse);

        // when & then
        mockMvc.perform(get("/organizations/{organizationUuid}/feedbacks", organizationUuid)
                        .requestAttr(GUEST_ID.getValue(), guestUuid)
                        .param("size", "10")
                        .param("sortBy", "LATEST"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.feedbacks.length()").value(0))
                .andExpect(jsonPath("$.data.hasNext").value(false));
    }

    @Test
    @DisplayName("피드백을 성공적으로 생성한다")
    void create_feedback_success() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        UUID guestUuid = UUID.randomUUID();
        CreateFeedbackRequest request = new CreateFeedbackRequest("피드백", false, "유저", "건의", null);

        CreateFeedbackResponse response = new CreateFeedbackResponse(
                1L, "피드백", ProcessStatus.WAITING, false, "유저", LocalDateTime.now(), "기타", "이미지url"
        );

        given(userFeedbackService.create(eq(request),eq(organizationUuid), eq(new GuestInfo(guestUuid))))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/organizations/{organizationUuid}/feedbacks", organizationUuid)
                        .requestAttr(GUEST_ID.getValue(), guestUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.feedbackId").exists())
                .andExpect(jsonPath("$.data.content").value("피드백"));
    }

    @Test
    @DisplayName("사용자가 비밀 피드백을 성공적으로 생성한다")
    void user_create_secret_feedback_success() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        UUID guestUuid = UUID.randomUUID();
        CreateFeedbackRequest request = new CreateFeedbackRequest("비밀 피드백입니다", true, "테스트유저", "건의", "https://example.com/image.png");

        CreateFeedbackResponse response = new CreateFeedbackResponse(
                1L, "피드백", ProcessStatus.WAITING, true, "유저", LocalDateTime.now(), "기타", "이미지url"
        );

        given(userFeedbackService.create(eq(request),eq(organizationUuid), eq(new GuestInfo(guestUuid))))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/organizations/{organizationUuid}/feedbacks", organizationUuid)
                        .requestAttr(GUEST_ID.getValue(), guestUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.isSecret").value(true));
    }

    @Test
    @DisplayName("피드백 목록을 최신순으로 반환된다")
    void get_feedbacks_ordered_by_latest() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        UUID guestUuid = UUID.randomUUID();

        UserFeedbackListResponse response = new UserFeedbackListResponse(
                List.of(
                        createUserFeedbackItem(3L, "피드백3", null),
                        createUserFeedbackItem(2L, "피드백2", null),
                        createUserFeedbackItem(1L, "피드백1", null)
                ), false, 1L
        );

        given(userFeedbackService.getFeedbackPage(
                eq(organizationUuid),
                eq(10),
                isNull(),
                any(),
                eq(FeedbackSortType.LATEST),
                any()
        )).willReturn(response);

        // when & then
        mockMvc.perform(get("/organizations/{organizationUuid}/feedbacks", organizationUuid)
                        .requestAttr(GUEST_ID.getValue(), guestUuid)
                        .param("size", "10")
                        .param("sortBy", "LATEST"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.feedbacks[0].feedbackId").value(3))
                .andExpect(jsonPath("$.data.feedbacks[1].feedbackId").value(2))
                .andExpect(jsonPath("$.data.feedbacks[2].feedbackId").value(1));
    }

    @Test
    @DisplayName("피드백 목록을 오래된순으로 반환된다")
    void get_feedbacks_ordered_by_oldest() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        UUID guestUuid = UUID.randomUUID();

        UserFeedbackListResponse response = new UserFeedbackListResponse(
                List.of(
                        createUserFeedbackItem(1L, "피드백1", null),
                        createUserFeedbackItem(2L, "피드백2", null),
                        createUserFeedbackItem(3L, "피드백3", null)
                ), false, 3L
        );

        given(userFeedbackService.getFeedbackPage(
                eq(organizationUuid),
                eq(10),
                isNull(),
                any(),
                eq(FeedbackSortType.OLDEST),
                any()
        )).willReturn(response);

        // when & then
        mockMvc.perform(get("/organizations/{organizationUuid}/feedbacks", organizationUuid)
                        .requestAttr(GUEST_ID.getValue(), guestUuid)
                        .param("size", "10")
                        .param("sortBy", "OLDEST"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.feedbacks[0].feedbackId").value(1))
                .andExpect(jsonPath("$.data.feedbacks[1].feedbackId").value(2))
                .andExpect(jsonPath("$.data.feedbacks[2].feedbackId").value(3));
    }

    @Test
    @DisplayName("피드백 목록을 좋아요 많은 순으로 반환된다")
    void get_feedbacks_ordered_by_likes() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        UUID guestUuid = UUID.randomUUID();

        UserFeedbackListResponse response = new UserFeedbackListResponse(
                List.of(
                        createUserFeedbackItemWithLikes(2L, 10),
                        createUserFeedbackItemWithLikes(1L, 5),
                        createUserFeedbackItemWithLikes(3L, 3)
                ), false, 3L
        );

        given(userFeedbackService.getFeedbackPage(
                eq(organizationUuid),
                eq(10),
                isNull(),
                any(),
                eq(FeedbackSortType.LIKES),
                any()
        )).willReturn(response);

        // when & then
        mockMvc.perform(get("/organizations/{organizationUuid}/feedbacks", organizationUuid)
                        .requestAttr(GUEST_ID.getValue(), guestUuid)
                        .param("size", "10")
                        .param("sortBy", "LIKES"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.feedbacks[0].likeCount").value(10))
                .andExpect(jsonPath("$.data.feedbacks[1].likeCount").value(5))
                .andExpect(jsonPath("$.data.feedbacks[2].likeCount").value(3));
    }

    @Test
    @DisplayName("피드백 조회 시 imageUrl 필드가 포함되어야 한다")
    void get_feedbacks_with_imageUrl() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        UUID guestUuid = UUID.randomUUID();
        String imageUrl = "https://example.com/test-image.png";

        UserFeedbackListResponse response = new UserFeedbackListResponse(
                List.of(createUserFeedbackItem(1L, "이미지 있는 피드백", imageUrl)),
                false, 1L
        );

        given(userFeedbackService.getFeedbackPage(
                eq(organizationUuid),
                eq(10),
                isNull(),
                any(),
                eq(FeedbackSortType.LATEST),
                any()
        )).willReturn(response);

        // when & then
        mockMvc.perform(get("/organizations/{organizationUuid}/feedbacks", organizationUuid)
                        .requestAttr(GUEST_ID.getValue(), guestUuid)
                        .param("size", "10")
                        .param("sortBy", "LATEST"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.feedbacks[0].imageUrl").value(imageUrl));
    }

    @Test
    @DisplayName("피드백 조회 시 imageUrl 필드가 없으면 null이어야 한다")
    void get_feedbacks_without_imageUrl() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        UUID guestUuid = UUID.randomUUID();

        UserFeedbackListResponse response = new UserFeedbackListResponse(
                List.of(createUserFeedbackItem(1L, "이미지 없는 피드백", null)),
                false, 1L
        );

        given(userFeedbackService.getFeedbackPage(
                eq(organizationUuid),
                eq(10),
                isNull(),
                any(),
                eq(FeedbackSortType.LATEST),
                any()
        )).willReturn(response);

        // when & then
        mockMvc.perform(get("/organizations/{organizationUuid}/feedbacks", organizationUuid)
                        .requestAttr(GUEST_ID.getValue(), guestUuid)
                        .param("size", "10")
                        .param("sortBy", "LATEST"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.feedbacks[0].imageUrl").isEmpty()); // null 확인
    }


private UserFeedbackItem createUserFeedbackItem(Long id, String content, String imageUrl) {
    return new UserFeedbackItem(
            id,
            content,
            ProcessStatus.WAITING,
            false,
            0,
            "유저",
            LocalDateTime.now(),
            "건의",
            null,
            imageUrl
    );
}

    private UserFeedbackItem createUserFeedbackItemWithLikes(Long id, int likeCount) {
        return new UserFeedbackItem(
                id,
                "내용",
                ProcessStatus.WAITING,
                false,
                likeCount,
                "유저",
                LocalDateTime.now(),
                "건의",
                null,
                null
        );
    }
}
