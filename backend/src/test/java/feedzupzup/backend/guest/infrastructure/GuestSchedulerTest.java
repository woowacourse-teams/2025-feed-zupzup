package feedzupzup.backend.guest.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.util.CurrentDateTime;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.guest.domain.guest.GuestActiveTracker;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
import feedzupzup.backend.guest.domain.like.LikeHistory;
import feedzupzup.backend.guest.domain.like.LikeHistoryRepository;
import feedzupzup.backend.guest.domain.write.WriteHistory;
import feedzupzup.backend.guest.domain.write.WriteHistoryRepository;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GuestSchedulerTest extends ServiceIntegrationHelper {

    @Autowired
    private GuestScheduler guestScheduler;

    @Autowired
    private GuestActiveTracker guestActiveTracker;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private WriteHistoryRepository writeHistoryRepository;

    @Autowired
    private LikeHistoryRepository likeHistoryRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    private Guest guest1;
    private Guest guest2;

    private Organization organization;
    private OrganizationCategory organizationCategory;
    private Feedback feedback;

    @BeforeEach
    void init() {
        guestActiveTracker.clear();
        guest1 = guestRepository.save(new Guest(UUID.randomUUID(), CurrentDateTime.create()));
        guest2 = guestRepository.save(new Guest(UUID.randomUUID(), CurrentDateTime.create()));
        organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
        organizationCategory = organizationCategoryRepository.save(OrganizationCategoryFixture.createOrganizationCategory(organization));
        feedback = feedbackRepository.save(FeedbackFixture.createFeedbackWithLikes(organization, organizationCategory, 0));
    }


    @Nested
    @DisplayName("스케줄러 실행 테스트")
    class SyncDailyActivityTest {

        @Test
        @DisplayName("활동한 게스트가 있을 때 트래커가 초기화된다")
        void syncDailyActivity_withActiveGuests_success() {
            // given
            guestActiveTracker.trackActivity(guest1.getGuestUuid());
            guestActiveTracker.trackActivity(guest2.getGuestUuid());
            assertThat(guestActiveTracker.getTodayActiveGuests()).hasSize(2);

            // when
            guestScheduler.updateDailyActiveGuestConnectedTime();

            // then
            assertThat(guestActiveTracker.getTodayActiveGuests()).isEmpty();
        }

        @Test
        @DisplayName("같은 게스트가 여러 번 활동해도 한 번만 기록된다")
        void syncDailyActivity_withDuplicateActivity_trackOnce() {
            // given
            guestActiveTracker.trackActivity(guest1.getGuestUuid());
            guestActiveTracker.trackActivity(guest1.getGuestUuid());

            // when
            final Set<UUID> activeGuests = guestActiveTracker.getTodayActiveGuests();

            // then
            assertThat(activeGuests).hasSize(1);
        }

        @Test
        @DisplayName("스케줄러 실행 후 트래커가 초기화된다")
        void syncDailyActivity_clearsTracker() {
            // given
            guestActiveTracker.trackActivity(guest1.getGuestUuid());
            assertThat(guestActiveTracker.getTodayActiveGuests()).hasSize(1);

            // when
            guestScheduler.updateDailyActiveGuestConnectedTime();

            // then
            assertThat(guestActiveTracker.getTodayActiveGuests()).isEmpty();
        }
    }

    @Nested
    @DisplayName("비활성 Guest 삭제 스케줄러 테스트")
    class RemoveUnActiveGuestTest {

        @Test
        @DisplayName("3개월 이상 접속하지 않은 Guest는 삭제된다")
        void removeUnActiveGuest_withOldGuest_success() {
            // given - 3개월 이상 접속하지 않은 Guest 생성
            final Guest unActiveGuest = createAndSaveUnActiveGuest();

            // when
            guestScheduler.removeUnActiveGuest();

            // then
            assertThat(guestRepository.findById(unActiveGuest.getId())).isEmpty();
        }

        @Test
        @DisplayName("3개월 이내에 접속한 Guest는 삭제되지 않는다")
        void removeUnActiveGuest_withRecentGuest_notDeleted() {
            // given - 2개월 전에 접속한 Guest 생성
            final Guest activeGuest = createAndSaveActiveGuest();

            // when
            guestScheduler.removeUnActiveGuest();

            // then
            assertThat(guestRepository.findById(activeGuest.getId())).isPresent();
        }

        @Test
        @DisplayName("비활성 Guest의 WriteHistory도 함께 삭제된다")
        void removeUnActiveGuest_deletesWriteHistory() {
            // given - 3개월 이상 접속하지 않은 Guest와 WriteHistory 생성
            final Guest unActiveGuest = createAndSaveUnActiveGuest();
            final WriteHistory writeHistory = createAndSaveWriteHistory(unActiveGuest,
                    feedback);

            // when
            guestScheduler.removeUnActiveGuest();

            // then
            assertThat(guestRepository.findById(unActiveGuest.getId())).isEmpty();
            assertThat(writeHistoryRepository.findById(writeHistory.getId())).isEmpty();
        }

        @Test
        @DisplayName("비활성 Guest의 LikeHistory도 함께 삭제된다")
        void removeUnActiveGuest_deletesLikeHistory() {
            // given - 3개월 이상 접속하지 않은 Guest와 LikeHistory 생성
            final Guest unActiveGuest = createAndSaveUnActiveGuest();
            final LikeHistory likeHistory = createAndSaveLikeHistory(unActiveGuest, feedback);

            // when
            guestScheduler.removeUnActiveGuest();

            // then
            assertThat(guestRepository.findById(unActiveGuest.getId())).isEmpty();
            assertThat(likeHistoryRepository.findById(likeHistory.getId())).isEmpty();
        }

        @Test
        @DisplayName("활성 Guest의 WriteHistory는 삭제되지 않는다")
        void removeUnActiveGuest_keepsActiveGuestWriteHistory() {
            // given - 2개월 전에 접속한 Guest와 WriteHistory 생성
            final Guest activeGuest = createAndSaveActiveGuest();
            final WriteHistory writeHistory = createAndSaveWriteHistory(activeGuest, feedback);

            // when
            guestScheduler.removeUnActiveGuest();

            // then
            assertThat(guestRepository.findById(activeGuest.getId())).isPresent();
            assertThat(writeHistoryRepository.findById(writeHistory.getId())).isPresent();
        }

        @Test
        @DisplayName("활성 Guest의 LikeHistory는 삭제되지 않는다")
        void removeUnActiveGuest_keepsActiveGuestLikeHistory() {
            // given - 2개월 전에 접속한 Guest와 LikeHistory 생성
            final Guest activeGuest = createAndSaveActiveGuest();
            final LikeHistory likeHistory = createAndSaveLikeHistory(activeGuest, feedback);

            // when
            guestScheduler.removeUnActiveGuest();

            // then
            assertThat(guestRepository.findById(activeGuest.getId())).isPresent();
            assertThat(likeHistoryRepository.findById(likeHistory.getId())).isPresent();
        }

        @Test
        @DisplayName("비활성 Guest와 활성 Guest가 섞여 있을 때 비활성 Guest만 삭제된다")
        void removeUnActiveGuest_withMixedGuests_deletesOnlyInactive() {
            // given
            final Guest activeGuest = createAndSaveActiveGuest();
            final Guest unActiveGuest = createAndSaveUnActiveGuest();

            final Feedback feedback1 = feedbackRepository.save(
                    FeedbackFixture.createFeedbackWithLikes(organization, organizationCategory, 0)
            );
            final Feedback feedback2 = feedbackRepository.save(
                    FeedbackFixture.createFeedbackWithLikes(organization, organizationCategory, 0)
            );

            final WriteHistory unActiveGuestWriteHistory = createAndSaveWriteHistory(unActiveGuest, feedback1);
            final WriteHistory activeGuestWriteHistory = createAndSaveWriteHistory(activeGuest, feedback2);

            // when
            guestScheduler.removeUnActiveGuest();

            // then
            assertAll(
                    () -> assertThat(guestRepository.findById(unActiveGuest.getId())).isEmpty(),
                    () -> assertThat(guestRepository.findById(activeGuest.getId())).isPresent(),
                    () -> assertThat(writeHistoryRepository.findById(unActiveGuestWriteHistory.getId())).isEmpty(),
                    () -> assertThat(writeHistoryRepository.findById(activeGuestWriteHistory.getId())).isPresent()
            );
        }

        @Test
        @DisplayName("비활성 Guest가 없으면 아무것도 삭제되지 않는다")
        void removeUnActiveGuest_withNoInactiveGuests_deletesNothing() {
            // given - 모든 Guest가 최근에 접속함
            final long initialGuestCount = guestRepository.count();

            // when
            guestScheduler.removeUnActiveGuest();

            // then
            assertThat(guestRepository.count()).isEqualTo(initialGuestCount);
        }
    }

    private LikeHistory createAndSaveLikeHistory(Guest guest, Feedback feedback) {
        return likeHistoryRepository.save(new LikeHistory(guest, feedback));
    }

    private WriteHistory createAndSaveWriteHistory(Guest guest, Feedback feedback) {
        return writeHistoryRepository.save(new WriteHistory(guest, feedback));
    }

    private Guest createAndSaveActiveGuest() {
        final LocalDateTime currentDate = CurrentDateTime.create();
        return guestRepository.save(new Guest(UUID.randomUUID(), currentDate));
    }

    private Guest createAndSaveUnActiveGuest() {
        final LocalDateTime fourMonthsAgo = CurrentDateTime.create().minusMonths(4);
        return guestRepository.save(new Guest(UUID.randomUUID(), fourMonthsAgo));
    }
}
