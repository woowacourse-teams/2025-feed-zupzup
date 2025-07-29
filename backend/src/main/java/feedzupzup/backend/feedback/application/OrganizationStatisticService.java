package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedbacks;
import feedzupzup.backend.feedback.dto.response.StatisticResponse;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.place.domain.Place;
import feedzupzup.backend.place.domain.PlaceRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrganizationStatisticService {

    private final FeedBackRepository feedBackRepository;
    private final PlaceRepository placeRepository;

    public StatisticResponse calculateStatistic(final Long placeId, final int period) {
        final Place place = findPlaceBy(placeId);
        final int targetPeriod = period - 1;
        final LocalDateTime targetDateTime = LocalDate.now().minusDays(targetPeriod).atStartOfDay();
        final Feedbacks feedbacks = new Feedbacks(
                feedBackRepository.findByIdAndPostedAtAfter(place.getId(), targetDateTime)
        );
        return StatisticResponse.of(feedbacks);
    }

    private Place findPlaceBy(final Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("장소를 찾을 수 없습니다."));
    }
}
