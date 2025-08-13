package feedzupzup.backend.organization.application;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.dto.request.CreateOrganizationRequest;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.domain.OrganizerRole;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizerRepository organizerRepository;
    private final AdminRepository adminRepository;
    private final OrganizationCategoryRepository organizationCategoryRepository;

    @Transactional
    public void saveOrganization(final CreateOrganizationRequest request, final Long adminId) {
        final Organization organization = request.toOrganization();
        final List<String> categories = request.categories();

        saveOrganizationCategory(categories, organization);

        final Admin admin = findAdminBy(adminId);
        Organizer organizer = new Organizer(
                organization,
                admin,
                OrganizerRole.OWNER
        );

        organizationRepository.save(organization);
        organizerRepository.save(organizer);
    }

    private Admin findAdminBy(final Long adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID(id = " + adminId + ")인 admin을 찾을 수 없습니다."));
    }

    private void saveOrganizationCategory(final List<String> categories, final Organization organization) {
        for (String category : categories) {
            final OrganizationCategory organizationCategory = new OrganizationCategory(
                    organization,
                    Category.findCategoryBy(category)
            );
            organization.addOrganizationCategory(organizationCategory);
            organizationCategoryRepository.save(organizationCategory);
        }
    }
}
