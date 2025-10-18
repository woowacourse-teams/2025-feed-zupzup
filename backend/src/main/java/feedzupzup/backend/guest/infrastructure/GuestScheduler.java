package feedzupzup.backend.guest.infrastructure;

import feedzupzup.backend.global.util.CurrentDateTime;
import feedzupzup.backend.guest.domain.guest.GuestActiveTracker;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
import feedzupzup.backend.guest.domain.like.LikeHistoryRepository;
import feedzupzup.backend.guest.domain.write.WriteHistoryRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class GuestScheduler {

    private final GuestActiveTracker guestActiveTracker;
    private final GuestRepository guestRepository;
    private final WriteHistoryRepository writeHistoryRepository;
    private final LikeHistoryRepository likeHistoryRepository;

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void updateDailyActiveGuestConnectedTime() {
        log.info("Active 유저 삽입 스케줄러 작동 시작");
        final Set<UUID> activeGuests = guestActiveTracker.getTodayActiveGuests();

        if (activeGuests.isEmpty()) {
            log.info("Active 유저가 존재하지 않아 금일 스케줄러 작동 패스");
            return;
        }
        guestRepository.updateConnectedTimeForGuests(activeGuests, CurrentDateTime.create());
        guestActiveTracker.clear();
        log.info("Active 유저 삽입 스케줄러 작동 완료");
    }

    @Scheduled(cron = "0 5 1 * * *")
    @Transactional
    public void removeUnActiveGuest() {
        log.info("비활성 유저 삭제 스케줄러 작동 시작");
        final LocalDateTime targetDateTime = CurrentDateTime.create().minusMonths(3);
        final List<Long> unActivateGuests = guestRepository.findAllByConnectedTimeBefore(targetDateTime);

        if (unActivateGuests.isEmpty()) {
            log.info("비활성 유저가 존재하지 않아 스케줄러 작동 패스");
            return;
        }

        writeHistoryRepository.deleteByGuestIdIn(unActivateGuests);
        likeHistoryRepository.deleteByGuestIdIn(unActivateGuests);
        guestRepository.deleteAllById(unActivateGuests);
        log.info("비활성 유저 {}명 삭제 완료 - IDs: {}", unActivateGuests.size(), unActivateGuests);
    }
}
