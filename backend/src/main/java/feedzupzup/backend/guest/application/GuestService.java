package feedzupzup.backend.guest.application;

import feedzupzup.backend.global.domain.LockRepository;
import feedzupzup.backend.global.util.CurrentDateTime;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.guest.domain.guest.GuestActiveTracker;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
import feedzupzup.backend.guest.dto.GuestInfo;
import feedzupzup.backend.guest.dto.response.LikeHistoryResponse;
import feedzupzup.backend.feedback.dto.response.MyFeedbackListResponse;
import feedzupzup.backend.guest.domain.like.LikeHistory;
import feedzupzup.backend.guest.domain.like.LikeHistoryRepository;
import feedzupzup.backend.guest.domain.write.WriteHistory;
import feedzupzup.backend.guest.domain.write.WriteHistoryRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GuestService {

    private static final String GUEST_REMOVE_KEY = "guest_remove_lock";
    private static final String GUEST_STATUS_UPDATE_KEY = "guest_last_connected_lock";

    private final GuestRepository guestRepository;
    private final WriteHistoryRepository writeHistoryRepository;
    private final LikeHistoryRepository likeHistoryRepository;
    private final LockRepository lockRepository;
    private final GuestActiveTracker guestActiveTracker;

    @Transactional
    public void save(final UUID guestUuid) {
        final Guest guest = new Guest(guestUuid, CurrentDateTime.create());
        guestRepository.save(guest);
    }

    public MyFeedbackListResponse getMyFeedbackPage(
            final UUID organizationUuid,
            final GuestInfo guestInfo
    ) {
        final List<WriteHistory> writeHistories = writeHistoryRepository.findWriteHistoriesBy(
                guestInfo.guestUuid(), organizationUuid);
        return MyFeedbackListResponse.fromHistory(writeHistories);
    }

    public LikeHistoryResponse findGuestLikeHistories(
            final UUID organizationUuid,
            final GuestInfo guestInfo
    ) {
        final List<LikeHistory> likeHistories = likeHistoryRepository.findLikeHistoriesBy(
                guestInfo.guestUuid(), organizationUuid);
        return LikeHistoryResponse.from(likeHistories);
    }

    public boolean isSavedGuest(final UUID guestUuid) {
        return guestRepository.existsByGuestUuid(guestUuid);
    }

    @Transactional
    public boolean processUpdateWithLock() {
        Integer result = lockRepository.getLock(GUEST_STATUS_UPDATE_KEY, 30);
        if (result != null && result == 1) {
            try {
                final Set<UUID> activeGuests = guestActiveTracker.getTodayActiveGuests();
                if (activeGuests.isEmpty()) {
                    log.info("금일 접속 사용자 수가 없습니다.");
                    return true;
                }
                final int updateGuestsCount = guestRepository.updateConnectedTimeForGuests(
                        activeGuests,
                        CurrentDateTime.create()
                );
                log.info("금일 접속 사용자 수 : " + updateGuestsCount);
                guestActiveTracker.clear();
                return true;
            } finally {
                lockRepository.releaseLock(GUEST_STATUS_UPDATE_KEY);
            }
        }
        log.error("락 획득 실패 (timeout)");
        return false;
    }

    @Transactional
    public int removeUnActiveGuest() {
        Integer result = lockRepository.getLock(GUEST_REMOVE_KEY, 0);
        if (result != null && result == 1) {
            try {
                final LocalDateTime targetDateTime = CurrentDateTime.create().minusMonths(3);
                final List<Long> unActivateGuests = guestRepository.findAllByConnectedTimeBefore(
                        targetDateTime);
                if (unActivateGuests.isEmpty()) {
                    return 0;
                }
                writeHistoryRepository.deleteByGuestIdIn(unActivateGuests);
                likeHistoryRepository.deleteByGuestIdIn(unActivateGuests);
                guestRepository.deleteAllById(unActivateGuests);
                return unActivateGuests.size();
            } finally {
                lockRepository.releaseLock(GUEST_REMOVE_KEY);
            }
        }
        return 0;
    }
}
