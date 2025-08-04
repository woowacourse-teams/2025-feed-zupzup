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

    public boolean contains(final String category) {
        return availableCategories.stream()
                .anyMatch(result -> result.isSameCategory(category));
    }

    public Category findCategoryBy(final String category) {
        final AvailableCategory availableCategory = availableCategories.stream()
                .filter(result -> result.isSameCategory(category))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 카테고리입니다."));
        return availableCategory.getCategory();
    }
}
