package feedzupzup.backend.feedback.controller;

import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.ADMIN_ID;
import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.ORGANIZATION_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import feedzupzup.backend.auth.presentation.resolver.AdminSessionArgumentResolver;
import feedzupzup.backend.auth.presentation.resolver.OrganizerArgumentResolver;
import feedzupzup.backend.feedback.application.AdminFeedbackService;
import feedzupzup.backend.feedback.domain.vo.FeedbackDownloadJob;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortType;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse.AdminFeedbackItem;
import feedzupzup.backend.feedback.dto.response.ClusterFeedbacksResponse;
import feedzupzup.backend.feedback.dto.response.ClustersResponse;
import feedzupzup.backend.feedback.dto.response.ClustersResponse.ClusterResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackCommentResponse;
import feedzupzup.backend.feedback.exception.FeedbackException.DownloadJobNotCompletedException;
import feedzupzup.backend.global.exception.GlobalExceptionHandler;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import java.time.LocalDateTime;
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
class AdminFeedbackControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private AdminFeedbackService adminFeedbackService;

    @InjectMocks
    private AdminFeedbackController adminFeedbackController;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(adminFeedbackController)
                .setCustomArgumentResolvers(new AdminSessionArgumentResolver(), new OrganizerArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("관리자가 피드백을 성공적으로 삭제한다")
    void admin_delete_feedback_success() throws Exception {
        // given
        Long feedbackId = 1L;

        // when & then
        mockMvc.perform(delete("/admin/feedbacks/{feedbackId}", feedbackId).requestAttr(ADMIN_ID.getValue(), 1L))
                .andDo(print())
                .andExpect(status().isOk());

        verify(adminFeedbackService).delete(anyLong(), eq(feedbackId));
    }

    @Test
    @DisplayName("관리자가 피드백 목록을 성공적으로 조회한다")
    void admin_get_feedbacks_success() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        Long adminId = 1L;

        AdminFeedbackListResponse response = new AdminFeedbackListResponse(
                List.of(createAdminFeedbackItem(1L, "피드백1"), createAdminFeedbackItem(2L, "피드백2"), createAdminFeedbackItem(3L, "피드백3")),
                false, 1L
        );

        given(adminFeedbackService.getFeedbackPage(any(UUID.class), anyInt(), any(), any(), any()))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/admin/organizations/{organizationUuid}/feedbacks", organizationUuid)
                        .requestAttr(ADMIN_ID.getValue(), adminId)             // AdminSession용
                        .requestAttr(ORGANIZATION_ID.getValue(), organizationUuid) // OrganizerInfo용
                        .param("size", "10")
                        .param("sortBy", "LATEST"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.feedbacks.length()").value(3));
    }

    @Test
    @DisplayName("관리자가 답글을 달 수 있어야 한다.")
    void admin_comment() throws Exception {
        // given
        Long feedbackId = 1L;
        Long adminId = 1L;
        UpdateFeedbackCommentRequest request = new UpdateFeedbackCommentRequest("testRequest");
        UpdateFeedbackCommentResponse response = new UpdateFeedbackCommentResponse(feedbackId, "testRequest");

        given(adminFeedbackService.updateFeedbackComment(anyLong(), any(UpdateFeedbackCommentRequest.class), anyLong()))
                .willReturn(response);

        // when & then
        mockMvc.perform(patch("/admin/feedbacks/{feedbackId}/comment", feedbackId)
                        .requestAttr(ADMIN_ID.getValue(), adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.comment").value("testRequest"))
                .andExpect(jsonPath("$.data.feedbackId").value(1));
    }

    @Test
    @DisplayName("관리자 피드백 목록을 최신순으로 반환된다")
    void admin_get_feedbacks_ordered_by_latest() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();

        AdminFeedbackListResponse response = new AdminFeedbackListResponse(
                List.of(
                        createAdminFeedbackItem(3L, "세 번째 피드백"),
                        createAdminFeedbackItem(2L, "두 번째 피드백"),
                        createAdminFeedbackItem(1L, "첫 번째 피드백")
                ),
                false,
                1L
        );

        given(adminFeedbackService.getFeedbackPage(
                eq(organizationUuid),
                eq(10),
                isNull(),
                any(),
                eq(FeedbackSortType.LATEST)
        )).willReturn(response);

        // when & then
        mockMvc.perform(get("/admin/organizations/{organizationUuid}/feedbacks", organizationUuid)
                        .requestAttr(ADMIN_ID.getValue(), 1L)
                        .requestAttr(ORGANIZATION_ID.getValue(), organizationUuid)
                        .param("size", "10")
                        .param("sortBy", "LATEST"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.feedbacks.length()").value(3))
                .andExpect(jsonPath("$.data.feedbacks[0].feedbackId").value(3))
                .andExpect(jsonPath("$.data.feedbacks[1].feedbackId").value(2))
                .andExpect(jsonPath("$.data.feedbacks[2].feedbackId").value(1));
    }

    @Test
    @DisplayName("관리자 피드백 목록을 오래된순으로 반환된다")
    void admin_get_feedbacks_ordered_by_oldest() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();

        // 응답 데이터 생성
        AdminFeedbackListResponse response = new AdminFeedbackListResponse(
                List.of(
                        createAdminFeedbackItem(1L, "첫 번째 피드백"),
                        createAdminFeedbackItem(2L, "두 번째 피드백"),
                        createAdminFeedbackItem(3L, "세 번째 피드백")
                ),
                false,
                3L
        );

        // Mocking 설정
        given(adminFeedbackService.getFeedbackPage(
                eq(organizationUuid),
                eq(10),
                isNull(),
                any(),
                eq(FeedbackSortType.OLDEST)
        )).willReturn(response);

        // when & then
        mockMvc.perform(get("/admin/organizations/{organizationUuid}/feedbacks", organizationUuid)
                        .requestAttr(ADMIN_ID.getValue(), 1L)
                        .requestAttr(ORGANIZATION_ID.getValue(), organizationUuid)
                        .param("size", "10")
                        .param("sortBy", "OLDEST"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.feedbacks.length()").value(3))
                .andExpect(jsonPath("$.data.feedbacks[0].feedbackId").value(1))
                .andExpect(jsonPath("$.data.feedbacks[1].feedbackId").value(2))
                .andExpect(jsonPath("$.data.feedbacks[2].feedbackId").value(3));
    }

    @Test
    @DisplayName("관리자 피드백 목록을 좋아요 많은 순으로 반환된다")
    void admin_get_feedbacks_ordered_by_likes() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();

        AdminFeedbackListResponse response = new AdminFeedbackListResponse(
                List.of(
                        createAdminFeedbackItemWithLikes(2L, 10),
                        createAdminFeedbackItemWithLikes(1L, 5),
                        createAdminFeedbackItemWithLikes(3L, 3)
                ),
                false,
                3L
        );

        // Mocking 설정
        given(adminFeedbackService.getFeedbackPage(
                eq(organizationUuid),
                eq(10),
                isNull(),
                any(),
                eq(FeedbackSortType.LIKES)
        )).willReturn(response);

        // when & then
        mockMvc.perform(get("/admin/organizations/{organizationUuid}/feedbacks", organizationUuid)
                        .requestAttr(ADMIN_ID.getValue(), 1L)
                        .requestAttr(ORGANIZATION_ID.getValue(), organizationUuid)
                        .param("size", "10")
                        .param("sortBy", "LIKES"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.feedbacks.length()").value(3))
                .andExpect(jsonPath("$.data.feedbacks[0].feedbackId").value(2))
                .andExpect(jsonPath("$.data.feedbacks[0].likeCount").value(10))
                .andExpect(jsonPath("$.data.feedbacks[1].feedbackId").value(1))
                .andExpect(jsonPath("$.data.feedbacks[1].likeCount").value(5))
                .andExpect(jsonPath("$.data.feedbacks[2].feedbackId").value(3))
                .andExpect(jsonPath("$.data.feedbacks[2].likeCount").value(3));
    }

    @Test
    @DisplayName("관리자가 모든 클러스터 대표 피드백을 성공적으로 조회한다")
    void admin_get_all_cluster_representatives_success() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();

        ClustersResponse response = new ClustersResponse(
                List.of(
                        new ClusterResponse(1L, "첫 번째 클러스터", 3L),
                        new ClusterResponse(2L, "두 번째 클러스터", 2L)
                )
        );

        given(adminFeedbackService.getTopClusters(
                eq(organizationUuid),
                eq(10)
        )).willReturn(response);

        // when & then
        mockMvc.perform(get("/admin/organizations/{organizationUuid}/clusters", organizationUuid)
                        .requestAttr(ADMIN_ID.getValue(), 1L)
                        .requestAttr(ORGANIZATION_ID.getValue(), organizationUuid)
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.clusterInfos.length()").value(2))
                .andExpect(jsonPath("$.data.clusterInfos[0].totalCount").value(3))
                .andExpect(jsonPath("$.data.clusterInfos[0].clusterId").value(1))
                .andExpect(jsonPath("$.data.clusterInfos[0].label").value("첫 번째 클러스터"))
                .andExpect(jsonPath("$.data.clusterInfos[1].totalCount").value(2))
                .andExpect(jsonPath("$.data.clusterInfos[1].clusterId").value(2))
                .andExpect(jsonPath("$.data.clusterInfos[1].label").value("두 번째 클러스터"));
    }

    @Test
    @DisplayName("관리자가 특정 클러스터의 모든 피드백을 성공적으로 조회한다")
    void admin_get_cluster_feedbacks_success() throws Exception {
        UUID organizationUuid = UUID.randomUUID();
        Long clusterId = 1L;

        ClusterFeedbacksResponse response = new ClusterFeedbacksResponse(
                List.of(
                        createAdminFeedbackItem(1L, "첫 번째 피드백"),
                        createAdminFeedbackItem(2L, "두 번째 피드백"),
                        createAdminFeedbackItem(3L, "세 번째 피드백")
                ),
                "테스트 클러스터",
                3L
        );

        given(adminFeedbackService.getFeedbacksByClusterId(eq(clusterId)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/admin/organizations/{organizationUuid}/clusters/{clusterId}",
                        organizationUuid, clusterId)
                        // [핵심 수정] 리졸버가 값을 꺼낼 수 있도록 Request Attribute 주입
                        .requestAttr(ADMIN_ID.getValue(), 1L)
                        .requestAttr(ORGANIZATION_ID.getValue(), organizationUuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.feedbacks.length()").value(3))
                .andExpect(jsonPath("$.data.label").value("테스트 클러스터"))
                .andExpect(jsonPath("$.data.totalCount").value(3))
                .andExpect(jsonPath("$.data.feedbacks[0].content").value("첫 번째 피드백"))
                .andExpect(jsonPath("$.data.feedbacks[1].content").value("두 번째 피드백"))
                .andExpect(jsonPath("$.data.feedbacks[2].content").value("세 번째 피드백"));
    }

    @Test
    @DisplayName("관리자가 존재하지 않는 클러스터를 조회하면 404 예외를 발생시킨다.")
    void admin_get_cluster_feedbacks_not_found() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        Long invalidClusterId = 99999L;

        given(adminFeedbackService.getFeedbacksByClusterId(invalidClusterId))
                .willThrow(new ResourceNotFoundException("존재하지 않는 클러스터입니다."));

        // when & then
        mockMvc.perform(get("/admin/organizations/{organizationUuid}/clusters/{clusterId}",
                        organizationUuid, invalidClusterId)
                        // 리졸버용 속성 주입
                        .requestAttr(ADMIN_ID.getValue(), 1L)
                        .requestAttr(ORGANIZATION_ID.getValue(), organizationUuid))
                .andDo(print())
                // [검증] ControllerAdvice가 예외를 잡아 404를 리턴하는지 확인
                .andExpect(status().isNotFound())
        // 필요하다면 에러 메시지 검증 추가
        // .andExpect(jsonPath("$.message").exists())
        ;
    }

    @Test
    @DisplayName("존재하지 않는 작업 ID로 상태 조회 시 404 에러")
    void get_download_job_status_not_found() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        String nonExistentJobId = UUID.randomUUID().toString();

        // [Mocking] 서비스가 "작업을 찾을 수 없음" 예외를 던지도록 설정
        given(adminFeedbackService.getDownloadJobStatus(eq(nonExistentJobId)))
                .willThrow(new ResourceNotFoundException("존재하지 않는 작업입니다."));

        // when & then
        mockMvc.perform(get("/admin/organizations/{organizationUuid}/download-jobs/{jobId}/status",
                        organizationUuid, nonExistentJobId)
                        // [필수] 리졸버가 통과하도록 속성 주입
                        .requestAttr(ADMIN_ID.getValue(), 1L)
                        .requestAttr(ORGANIZATION_ID.getValue(), organizationUuid))
                .andDo(print())
                // [검증] ControllerAdvice가 404로 잘 변환했는지 확인
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("비동기 다운로드 작업 생성 API 호출 성공")
    void create_download_job_success() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        String jobId = UUID.randomUUID().toString();

        // Mocking: any()보다 eq()가 더 정확합니다.
        given(adminFeedbackService.createDownloadJob(eq(organizationUuid)))
                .willReturn(jobId);

        // when & then
        mockMvc.perform(post("/admin/organizations/{organizationUuid}/download-jobs", organizationUuid)
                        // [수정] 리졸버를 위한 속성 주입 (sessionAttr 삭제)
                        .requestAttr(ADMIN_ID.getValue(), 1L)
                        .requestAttr(ORGANIZATION_ID.getValue(), organizationUuid))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.data.jobId").value(jobId))
                .andExpect(jsonPath("$.status").value(202));
    }

    @Test
    @DisplayName("다운로드 작업 상태 조회 API 호출 성공")
    void get_download_job_status_success() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        String jobId = UUID.randomUUID().toString();

        // Job 생성 및 진행률 업데이트
        FeedbackDownloadJob job = FeedbackDownloadJob.create(organizationUuid.toString());
        job.updateProgress(50);

        // Mocking: anyString() 대신 명확한 jobId 매칭 권장
        given(adminFeedbackService.getDownloadJobStatus(eq(jobId)))
                .willReturn(job);

        // when & then
        mockMvc.perform(get("/admin/organizations/{organizationUuid}/download-jobs/{jobId}/status",
                        organizationUuid, jobId)
                        // [핵심 수정] 리졸버가 값을 꺼낼 수 있게 속성 주입 (sessionAttr 제거)
                        .requestAttr(ADMIN_ID.getValue(), 1L)
                        .requestAttr(ORGANIZATION_ID.getValue(), organizationUuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.jobStatus").exists())
                .andExpect(jsonPath("$.data.progress").value(50))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("완료되지 않은 작업의 다운로드 요청 시 400 에러")
    void download_file_not_completed() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        String jobId = UUID.randomUUID().toString();

        // [Mocking] 서비스: "파일 다운로드 요청이 왔는데 아직 안 끝났으면 예외 던져!"
        given(adminFeedbackService.getDownloadUrl(jobId))
                .willThrow(new DownloadJobNotCompletedException("작업이 아직 완료되지 않았습니다."));

        // when & then
        mockMvc.perform(get("/admin/organizations/{organizationUuid}/download-jobs/{jobId}",
                        organizationUuid, jobId)
                        // [필수] 리졸버가 통과하도록 속성 주입
                        .requestAttr(ADMIN_ID.getValue(), 1L)
                        .requestAttr(ORGANIZATION_ID.getValue(), organizationUuid))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private AdminFeedbackItem createAdminFeedbackItem(Long feedbackId, String content) {
        return new AdminFeedbackItem(
                feedbackId,
                content,
                ProcessStatus.WAITING,
                false,
                0,
                "사용자",
                LocalDateTime.now(),
                "제안",
                null,
                null
        );
    }

    private AdminFeedbackItem createAdminFeedbackItemWithLikes(Long feedbackId, int likeCount) {
        return new AdminFeedbackItem(
                feedbackId,
                "피드백 내용",
                ProcessStatus.WAITING,
                false,
                likeCount,
                "사용자",
                LocalDateTime.now(),
                "제안",
                null,
                null
        );
    }
}
