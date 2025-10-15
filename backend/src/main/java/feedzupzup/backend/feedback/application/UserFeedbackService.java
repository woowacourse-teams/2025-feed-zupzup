package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.feedback.domain.vo.FeedbackSortType.LATEST;

import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackPage;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.event.FeedbackCreatedEvent2;
import feedzupzup.backend.feedback.domain.service.moderation.ContentFilter;
import feedzupzup.backend.feedback.domain.service.sort.FeedbackSortStrategy;
import feedzupzup.backend.feedback.domain.service.sort.FeedbackSortStrategyFactory;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortType;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.feedback.event.FeedbackCacheEvent;
import feedzupzup.backend.feedback.event.FeedbackCreatedEvent;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.global.log.BusinessActionLog;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
import feedzupzup.backend.guest.domain.write.WriteHistory;
import feedzupzup.backend.guest.domain.write.WriteHistoryRepository;
import feedzupzup.backend.guest.dto.GuestInfo;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserFeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final GuestRepository guestRepository;
    private final FeedbackSortStrategyFactory feedbackSortStrategyFactory;
    private final OrganizationRepository organizationRepository;
    private final WriteHistoryRepository writeHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ContentFilter contentFilter;

    @Transactional
    @BusinessActionLog
    public CreateFeedbackResponse create(
            final CreateFeedbackRequest request,
            final UUID organizationUuid,
            final GuestInfo guestInfo
    ) {
        final Guest guest = findGuestBy(guestInfo.guestUuid());
        final Organization organization = findOrganizationBy(organizationUuid);
        final Category category = Category.findCategoryBy(request.category());
        final OrganizationCategory organizationCategory = organization.findOrganizationCategoryBy(
                category);
        final String filteredContent = contentFilter.filter(request.content());
        final Feedback newFeedback = request.toFeedback(organization, organizationCategory, filteredContent);
        final Feedback savedFeedback = feedbackRepository.save(newFeedback);

        writeHistoryRepository.save(new WriteHistory(guest, savedFeedback));

        // 최신순 캐시 업데이트
        publishLatestFeedbackCacheEvent(FeedbackItem.from(savedFeedback), organizationUuid);

        // 새로운 피드백이 생성되면 이벤트 발행
        eventPublisher.publishEvent(new FeedbackCreatedEvent(organization.getId(), "피드줍줍"));
        //TODO : 위 피드백 생성 이벤트 리팩토링 필요
        eventPublisher.publishEvent(new FeedbackCreatedEvent2(savedFeedback.getId()));

        return CreateFeedbackResponse.from(savedFeedback);
    }

    public UserFeedbackListResponse getFeedbackPage(
            final UUID organizationUuid,
            final int size,
            final Long cursorId,
            final ProcessStatus status,
            final FeedbackSortType sortBy,
            final GuestInfo guestInfo
    ) {

        final Pageable pageable = createPageable(size);
        FeedbackSortStrategy feedbackSortStrategy = feedbackSortStrategyFactory.find(sortBy);
        final List<FeedbackItem> feedbackItems = feedbackSortStrategy.getSortedFeedbacks(organizationUuid, status, cursorId, pageable);
        final List<WriteHistory> writeHistoriesBy = writeHistoryRepository.findWriteHistoriesBy(guestInfo.guestUuid(),
                organizationUuid);
        final List<Long> myFeedbackIds = writeHistoriesBy.stream()
                .map(WriteHistory::getFeedback)
                .map(Feedback::getId)
                .toList();

        List<FeedbackItem> maskedFeedbackItems = feedbackItems.stream()
                .map((FeedbackItem feedbackItem) -> withMaskedContent(feedbackItem, myFeedbackIds))
                .toList();

        final FeedbackPage feedbackPage = FeedbackPage.createCursorPage(maskedFeedbackItems, size);

        return UserFeedbackListResponse.of(
                feedbackPage.getFeedbackItems(),
                feedbackPage.isHasNext(),
                feedbackPage.calculateNextCursorId()
        );
    }

    private FeedbackItem withMaskedContent(final FeedbackItem feedbackItem, final List<Long> myFeedbackIds) {
        if (myFeedbackIds.contains(feedbackItem.feedbackId())) {
            return feedbackItem.withMaskedContent();
        }
        return feedbackItem;
    }

    private Guest findGuestBy(final UUID guestUuid) {
        return guestRepository.findByGuestUuid(guestUuid)
                .orElseThrow(() -> new ResourceNotFoundException("게스트를 찾을 수 없습니다."));
    }

    private Organization findOrganizationBy(final UUID organizationUuid) {
        return organizationRepository.findByUuid(organizationUuid)
                .orElseThrow(() -> new ResourceNotFoundException("장소를 찾을 수 없습니다."));
    }

    private Pageable createPageable(int size) {
        return Pageable.ofSize(size + 1);
    }

    private void publishLatestFeedbackCacheEvent(final FeedbackItem feedbackItem, final UUID organizationUuid) {
        final FeedbackCacheEvent event = new FeedbackCacheEvent(feedbackItem, organizationUuid, LATEST);
        eventPublisher.publishEvent(event);
    }
}
