package feedzupzup.backend.category.domain;

import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

@Getter
public class AvailableCategories {

    private final Set<AvailableCategory> availableCategories;

    public AvailableCategories(final Set<AvailableCategory> availableCategories) {
        this.availableCategories = new HashSet<>(availableCategories);
    }

    public AvailableCategory findAvailableCategoryBy(final Category category) {
        return availableCategories.stream()
                .filter(result -> result.getCategory().equals(category))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 카테고리입니다."));
    }
}
