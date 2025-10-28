package feedzupzup.backend.guest.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.E2EHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.util.CookieUtilization;
import feedzupzup.backend.global.util.CurrentDateTime;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.guest.domain.guest.GuestActiveTracker;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
import feedzupzup.backend.guest.infrastructure.GuestScheduler;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class GuestSchedulerE2ETest extends E2EHelper {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private GuestActiveTracker guestActiveTracker;

    @Autowired
    private GuestScheduler guestScheduler;

    private Organization organization;
    private OrganizationCategory organizationCategory;

    @BeforeEach
    void init() {
        organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, Category.SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        // 테스트 전에 tracker 초기화
        guestActiveTracker.clear();
    }

    @Nested
    @DisplayName("Guest 활동 추적 테스트")
    class GuestActivityTracking {

        @Test
        @DisplayName("Guest가 쿠키를 가지고 API를 호출하면 GuestActiveTracker에 활동이 추적되어야 한다")
        void track_guest_activity_when_api_called() {
            // given - 먼저 쿠키 생성
            final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback = feedbackRepository.save(feedback);

            final String visitorCookie = given()
                    .log().all()
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            // when - 생성된 쿠키로 다시 API 호출 (이때 추적됨)
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback2 = feedbackRepository.save(feedback2);

            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, visitorCookie)
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback2.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());

            // then - tracker에 활동이 기록되어야 함
            final UUID guestUuid = UUID.fromString(visitorCookie);
            assertThat(guestActiveTracker.getTodayActiveGuests()).contains(guestUuid);
        }

        @Test
        @DisplayName("여러 Guest가 API를 호출하면 모두 추적되어야 한다")
        void track_multiple_guests_activity() {
            // given
            final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback = feedbackRepository.save(feedback);

            // when - 첫 번째 Guest 쿠키 생성
            final String guest1Cookie = given()
                    .log().all()
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            // 첫 번째 Guest가 쿠키로 다시 호출 (추적됨)
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback2 = feedbackRepository.save(feedback2);

            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, guest1Cookie)
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback2.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());

            // 두 번째 Guest 쿠키 생성
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback3 = feedbackRepository.save(feedback3);

            final String guest2Cookie = given()
                    .log().all()
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback3.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            // 두 번째 Guest가 쿠키로 다시 호출 (추적됨)
            final Feedback feedback4 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback4 = feedbackRepository.save(feedback4);

            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, guest2Cookie)
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback4.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());

            // then - 두 Guest 모두 추적되어야 함
            final UUID guest1Uuid = UUID.fromString(guest1Cookie);
            final UUID guest2Uuid = UUID.fromString(guest2Cookie);

            assertThat(guestActiveTracker.getTodayActiveGuests())
                    .contains(guest1Uuid, guest2Uuid)
                    .hasSize(2);
        }

        @Test
        @DisplayName("동일한 Guest가 여러 번 API를 호출해도 한 번만 추적되어야 한다")
        void track_same_guest_only_once() {
            // given
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback1 = feedbackRepository.save(feedback1);

            final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback2 = feedbackRepository.save(feedback2);

            // 먼저 쿠키 생성
            final String visitorCookie = given()
                    .log().all()
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback1.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            // when - 동일한 Guest가 여러 API 호출
            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, visitorCookie)
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback2.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());

            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, visitorCookie)
                    .when()
                    .get("/organizations/{organizationUuid}/feedbacks/my-likes",
                            organization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());

            // then - 한 번만 추적되어야 함
            final UUID guestUuid = UUID.fromString(visitorCookie);
            assertThat(guestActiveTracker.getTodayActiveGuests())
                    .contains(guestUuid)
                    .hasSize(1);
        }
    }

    @Nested
    @DisplayName("스케줄러 동작 테스트")
    class SchedulerBehavior {

        @Test
        @DisplayName("스케줄러 실행 시 활동한 Guest 들의 connectedTime이 업데이트되어야 한다")
        void update_connected_time_when_scheduler_runs() {
            // given - Guest가 활동하고 DB에 저장됨
            final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback = feedbackRepository.save(feedback);

            // 쿠키 생성
            final String visitorCookie = given()
                    .log().all()
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            // 쿠키로 다시 호출해서 추적되도록 함
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback2 = feedbackRepository.save(feedback2);

            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, visitorCookie)
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback2.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());

            final UUID guestUuid = UUID.fromString(visitorCookie);
            final Guest guest = guestRepository.findByGuestUuid(guestUuid)
                    .orElseThrow();

            final LocalDateTime originalConnectedTime = guest.getConnectedTime();

            // when - 스케줄러 실행
            guestScheduler.updateDailyActiveGuestConnectedTime();

            // then - connectedTime이 업데이트되어야 함
            final Guest updatedGuest = guestRepository.findByGuestUuid(guestUuid)
                    .orElseThrow();
            assertThat(updatedGuest.getConnectedTime())
                    .isAfter(originalConnectedTime);
        }

        @Test
        @DisplayName("스케줄러 실행 후 GuestActiveTracker가 초기화되어야 한다")
        void clear_tracker_after_scheduler_runs() {
            // given - Guest가 활동함
            final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback = feedbackRepository.save(feedback);

            // 쿠키 생성
            final String visitorCookie = given()
                    .log().all()
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            // 쿠키로 다시 호출해서 추적되도록 함
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback2 = feedbackRepository.save(feedback2);

            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, visitorCookie)
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback2.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());

            assertThat(guestActiveTracker.getTodayActiveGuests()).isNotEmpty();

            // when - 스케줄러 실행
            guestScheduler.updateDailyActiveGuestConnectedTime();

            // then - tracker가 비워져야 함
            assertThat(guestActiveTracker.getTodayActiveGuests()).isEmpty();
        }

        @Test
        @DisplayName("활동한 Guest가 없으면 스케줄러가 아무 작업도 하지 않아야 한다")
        void scheduler_does_nothing_when_no_active_guests() {
            // given - 활동한 Guest가 없음
            assertThat(guestActiveTracker.getTodayActiveGuests()).isEmpty();
            final long initialCount = guestRepository.count();

            // when - 스케줄러 실행
            guestScheduler.updateDailyActiveGuestConnectedTime();

            // then - DB 변경이 없어야 함
            assertThat(guestRepository.count()).isEqualTo(initialCount);
            assertThat(guestActiveTracker.getTodayActiveGuests()).isEmpty();
        }

        @Test
        @DisplayName("스케줄러가 여러 Guest의 connectedTime을 한 번에 업데이트해야 한다")
        void update_multiple_guests_connected_time() {
            // given - 여러 Guest가 활동함
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback1 = feedbackRepository.save(feedback1);

            // 첫 번째 Guest 쿠키 생성
            final String guest1Cookie = given()
                    .log().all()
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback1.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            // 첫 번째 Guest 활동
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback2 = feedbackRepository.save(feedback2);

            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, guest1Cookie)
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback2.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());

            // 두 번째 Guest 쿠키 생성
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback3 = feedbackRepository.save(feedback3);

            final String guest2Cookie = given()
                    .log().all()
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback3.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            // 두 번째 Guest 활동
            final Feedback feedback4 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback4 = feedbackRepository.save(feedback4);

            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, guest2Cookie)
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback4.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());

            final UUID guest1Uuid = UUID.fromString(guest1Cookie);
            final UUID guest2Uuid = UUID.fromString(guest2Cookie);

            final LocalDateTime guest1OriginalTime = guestRepository.findByGuestUuid(guest1Uuid)
                    .orElseThrow().getConnectedTime();
            final LocalDateTime guest2OriginalTime = guestRepository.findByGuestUuid(guest2Uuid)
                    .orElseThrow().getConnectedTime();

            // when - 스케줄러 실행
            guestScheduler.updateDailyActiveGuestConnectedTime();

            // then - 두 Guest 모두 업데이트되어야 함
            final Guest updatedGuest1 = guestRepository.findByGuestUuid(guest1Uuid)
                    .orElseThrow();
            final Guest updatedGuest2 = guestRepository.findByGuestUuid(guest2Uuid)
                    .orElseThrow();

            assertThat(updatedGuest1.getConnectedTime())
                    .isAfter(guest1OriginalTime);
            assertThat(updatedGuest2.getConnectedTime())
                    .isAfter(guest2OriginalTime);
            assertThat(guestActiveTracker.getTodayActiveGuests()).isEmpty();
        }

        @Test
        @DisplayName("활동하지 않은 Guest의 connectedTime은 업데이트되지 않아야 한다")
        void inactive_guest_connected_time_not_updated() {
            // given - Guest1은 활동하지만 Guest2는 활동하지 않음
            final UUID inactiveGuestUuid = UUID.randomUUID();
            final LocalDateTime inactiveGuestTime = CurrentDateTime.create();
            final Guest inactiveGuest = new Guest(inactiveGuestUuid, inactiveGuestTime);
            guestRepository.save(inactiveGuest);

            final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback = feedbackRepository.save(feedback);

            // 활동한 Guest - 쿠키 생성
            final String activeCookie = given()
                    .log().all()
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            // 활동한 Guest - 쿠키로 다시 호출
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback2 = feedbackRepository.save(feedback2);

            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, activeCookie)
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback2.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());

            // when - 스케줄러 실행
            guestScheduler.updateDailyActiveGuestConnectedTime();

            // then - 활동하지 않은 Guest의 connectedTime은 그대로여야 함
            final Guest unchangedGuest = guestRepository.findByGuestUuid(inactiveGuestUuid)
                    .orElseThrow();
            assertThat(unchangedGuest.getConnectedTime()).isEqualTo(inactiveGuestTime);
        }
    }
}
