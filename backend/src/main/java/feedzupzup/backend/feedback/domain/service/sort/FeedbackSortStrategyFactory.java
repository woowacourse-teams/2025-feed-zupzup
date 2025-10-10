package feedzupzup.backend.feedback.domain.service.sort;

import static java.util.stream.Collectors.toMap;

import feedzupzup.backend.feedback.domain.vo.FeedbackSortType;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class FeedbackSortStrategyFactory {

    private final Map<FeedbackSortType, FeedbackSortStrategy> strategies;

    public FeedbackSortStrategyFactory(final List<FeedbackSortStrategy> sortStrategies) {
        this.strategies = sortStrategies.stream()
                .collect(toMap(
                        FeedbackSortStrategy::getType,
                        strategy -> strategy
                ));
    }

    public FeedbackSortStrategy find(final FeedbackSortType sortBy) {
        if (!strategies.containsKey(sortBy)) {
            throw new IllegalArgumentException("해당 정렬(sortBy=" + sortBy + ")은 지원하지 않고 있습니다.");
        }
        return strategies.get(sortBy);
    }
}
