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
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final WriteHistoryRepository writeHistoryRepository;
    private final LikeHistoryRepository likeHistoryRepository;

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
            final UUID organizatioUuid,
            final GuestInfo guestInfo
    ) {
        final List<LikeHistory> likeHistories = likeHistoryRepository.findLikeHistoriesBy(
                guestInfo.guestUuid(), organizatioUuid);
        return LikeHistoryResponse.from(likeHistories);
    }

    public boolean isSavedGuest(final UUID guestUuid) {
        return guestRepository.existsByGuestUuid(guestUuid);
    }
}
