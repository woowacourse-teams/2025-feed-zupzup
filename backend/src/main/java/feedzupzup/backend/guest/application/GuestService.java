package feedzupzup.backend.guest.application;

import feedzupzup.backend.guest.dto.response.LikeHistoryResponse;
import feedzupzup.backend.feedback.dto.response.MyFeedbackListResponse;
import feedzupzup.backend.guest.domain.guest.Guest;
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

    private final WriteHistoryRepository writeHistoryRepository;
    private final LikeHistoryRepository likeHistoryRepository;

    public MyFeedbackListResponse getMyFeedbackPage(
            final UUID organizationUuid,
            final Guest guest
    ) {
        if (!guest.isPersisted()) {
            return MyFeedbackListResponse.from(Collections.emptyList());
        }
        final List<WriteHistory> writeHistories = writeHistoryRepository.findWriteHistoriesBy(
                guest.getId(), organizationUuid);
        return MyFeedbackListResponse.fromHistory(writeHistories);
    }

    public LikeHistoryResponse findGuestLikeHistories(
            final UUID organizatioUuid,
            final Guest guest
    ) {
        if (!guest.isPersisted()) {
            return LikeHistoryResponse.from(Collections.emptyList());
        }
        final List<LikeHistory> likeHistories = likeHistoryRepository.findLikeHistoriesBy(
                guest.getId(), organizatioUuid);
        return LikeHistoryResponse.from(likeHistories);
    }
}
