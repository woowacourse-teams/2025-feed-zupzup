package feedzupzup.backend.guest.application;

import feedzupzup.backend.feedback.dto.response.MyFeedbackListResponse;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
import feedzupzup.backend.guest.domain.write.WriteHistory;
import feedzupzup.backend.guest.domain.write.WriteHistoryRepository;
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

    public MyFeedbackListResponse getMyFeedbackPage(
            final UUID organizationUuid,
            final Guest guest
    ) {
        final List<WriteHistory> writeHistories = writeHistoryRepository.findWriteHistoriesBy(
                guest.getId(), organizationUuid);
        return MyFeedbackListResponse.fromHistory(writeHistories);
    }

}
