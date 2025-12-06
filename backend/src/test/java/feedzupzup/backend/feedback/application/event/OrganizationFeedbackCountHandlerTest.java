package feedzupzup.backend.feedback.application.event;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.application.UserFeedbackService;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.global.util.CurrentDateTime;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
import feedzupzup.backend.guest.dto.GuestInfo;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.domain.OrganizationStatistic;
import feedzupzup.backend.organization.domain.OrganizationStatisticRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.sse.service.SseService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class OrganizationFeedbackCountHandlerTest extends ServiceIntegrationHelper {

    @Autowired
    private UserFeedbackService userFeedbackService;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private OrganizationStatisticRepository organizationStatisticRepository;

    @Autowired
    private GuestRepository guestRepository;

    @MockitoBean
    private SseService sseService;

    private Organization organization;
    private OrganizationCategory organizationCategory;
    private Guest guest;

    @BeforeEach
    void init() {
        organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        organizationStatisticRepository.save(new OrganizationStatistic(organization));

        organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        // Guest 설정
        guest = new Guest(UUID.randomUUID(), CurrentDateTime.create());
        guestRepository.save(guest);
    }

    @Test
    @DisplayName("피드백 생성 시 SSE 알림이 발송된다")
    void when_feedback_created_then_sse_notification_sent() {
        // given
        final CreateFeedbackRequest request = new CreateFeedbackRequest(
                "새로운 피드백입니다",
                false,
                "테스트작성자",
                SUGGESTION.getKoreanName(),
                null
        );
        final GuestInfo guestInfo = new GuestInfo(guest.getGuestUuid());

        // when - 피드백 생성
        userFeedbackService.create(request, organization.getUuid(), guestInfo);

        // then - SSE 알림이 발송되었는지 검증 (비동기 처리를 위해 timeout 사용)
        verify(sseService, timeout(2000).times(1))
                .sendFeedbackNotificationToOrganization(
                        eq(organization.getId()),
                        eq(1L)
                );
    }

    @Test
    @DisplayName("여러 피드백을 연속으로 생성하면 각 생성마다 SSE 알림이 발송된다")
    void when_multiple_feedbacks_created_then_sse_notifications_sent() {
        // given
        final GuestInfo guestInfo = new GuestInfo(guest.getGuestUuid());

        // when - 3개의 피드백 생성
        userFeedbackService.create(
                new CreateFeedbackRequest("피드백 1", false, "작성자1", SUGGESTION.getKoreanName(), null),
                organization.getUuid(),
                guestInfo
        );
        userFeedbackService.create(
                new CreateFeedbackRequest("피드백 2", false, "작성자2", SUGGESTION.getKoreanName(), null),
                organization.getUuid(),
                guestInfo
        );
        userFeedbackService.create(
                new CreateFeedbackRequest("피드백 3", false, "작성자3", SUGGESTION.getKoreanName(), null),
                organization.getUuid(),
                guestInfo
        );

        // then - 각 피드백 생성마다 SSE 알림이 발송되었는지 검증
        verify(sseService, timeout(2000).times(1))
                .sendFeedbackNotificationToOrganization(eq(organization.getId()), eq(1L));
        verify(sseService, timeout(2000).times(1))
                .sendFeedbackNotificationToOrganization(eq(organization.getId()), eq(2L));
        verify(sseService, timeout(2000).times(1))
                .sendFeedbackNotificationToOrganization(eq(organization.getId()), eq(3L));
    }

    @Test
    @DisplayName("피드백 생성 시 올바른 카운트로 SSE 알림이 발송된다")
    void when_feedback_created_then_correct_count_sent() {
        // given
        final GuestInfo guestInfo = new GuestInfo(guest.getGuestUuid());
        final CreateFeedbackRequest request = new CreateFeedbackRequest(
                "테스트 피드백",
                false,
                "테스트작성자",
                SUGGESTION.getKoreanName(),
                null
        );

        // when
        userFeedbackService.create(request, organization.getUuid(), guestInfo);

        // then - 정확한 organizationUuid와 totalCount로 SSE 알림이 발송되었는지 검증
        verify(sseService, timeout(2000).times(1))
                .sendFeedbackNotificationToOrganization(
                        eq(organization.getId()),
                        eq(1L)
                );
    }
}
