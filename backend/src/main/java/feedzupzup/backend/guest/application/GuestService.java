package feedzupzup.backend.guest.application;

import feedzupzup.backend.global.util.CurrentDateTime;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
import feedzupzup.backend.guest.dto.GuestInfo;
import feedzupzup.backend.guest.dto.response.LikeHistoryResponse;
import feedzupzup.backend.feedback.dto.response.MyFeedbackListResponse;
import feedzupzup.backend.guest.domain.like.LikeHistory;
import feedzupzup.backend.guest.domain.like.LikeHistoryRepository;
import feedzupzup.backend.guest.domain.write.WriteHistory;
import feedzupzup.backend.guest.domain.write.WriteHistoryRepository;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuestService {

    private final GuestRepository guestRepository;
    private final WriteHistoryRepository writeHistoryRepository;
    private final LikeHistoryRepository likeHistoryRepository;

    @Transactional
    public void save(final UUID guestUuid) {
        Guest guest = new Guest(guestUuid, CurrentDateTime.create());
        guestRepository.save(guest);
    }

    public MyFeedbackListResponse getMyFeedbackPage(
            final UUID organizationUuid,
            final GuestInfo guestInfo
    ) {
        if (!isSavedGuest(guestInfo.guestUuid())) {
            return MyFeedbackListResponse.from(Collections.emptyList());
        }

        final List<WriteHistory> writeHistories = writeHistoryRepository.findWriteHistoriesBy(
                guestInfo.guestUuid(), organizationUuid);
        return MyFeedbackListResponse.fromHistory(writeHistories);
    }

    public LikeHistoryResponse findGuestLikeHistories(
            final UUID organizatioUuid,
            final GuestInfo guestInfo
    ) {
        if (!isSavedGuest(guestInfo.guestUuid())) {
            return LikeHistoryResponse.from(Collections.emptyList());
        }

        final List<LikeHistory> likeHistories = likeHistoryRepository.findLikeHistoriesBy(
                guestInfo.guestUuid(), organizatioUuid);
        return LikeHistoryResponse.from(likeHistories);
    }

    public boolean isSavedGuest(final UUID guestUuid) {
        return guestRepository.existsByGuestUuid(guestUuid);
    }
}
