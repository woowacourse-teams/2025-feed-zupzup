package feedzupzup.backend.category.application;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class OrganizationCategoryService {

    private final OrganizationCategoryRepository organizationCategoryRepository;

    @Transactional
    public void deleteAllByOrganizationIds(final List<Long> organizationIds) {
        organizationCategoryRepository.deleteAllByOrganizationIdIn(organizationIds);
    }

    @Transactional
    public void saveAll(final Set<OrganizationCategory> organizationCategories) {
        organizationCategoryRepository.saveAll(organizationCategories);
    }
}
