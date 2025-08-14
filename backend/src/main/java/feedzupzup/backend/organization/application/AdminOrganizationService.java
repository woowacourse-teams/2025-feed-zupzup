package feedzupzup.backend.organization.application;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.AdminOrganizationInfo;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationCategories;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.dto.request.CreateOrganizationRequest;
import feedzupzup.backend.organization.dto.response.AdminCreateOrganizationResponse;
import feedzupzup.backend.organization.dto.response.AdminInquireOrganizationResponse;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.domain.OrganizerRole;
import java.util.List;
import java.util.Set;
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
    public AdminCreateOrganizationResponse createOrganization(
            final CreateOrganizationRequest request,
            final Long adminId
    ) {
        final Organization organization = request.toOrganization();
        final Set<String> categories = request.categories();

        saveOrganizationCategory(categories, organization);

        final Admin admin = findAdminBy(adminId);
        final Organizer organizer = new Organizer(
                organization,
                admin,
                OrganizerRole.OWNER
        );

        final Organization savedOrganization = organizationRepository.save(organization);
        organizerRepository.save(organizer);
        return AdminCreateOrganizationResponse.from(savedOrganization);
    }

    public List<AdminInquireOrganizationResponse> getOrganizationsInfo(final Long adminId) {
        final Admin admin = findAdminBy(adminId);
        final List<AdminOrganizationInfo> adminOrganizationInfos = organizationRepository.getAdminOrganizationInfos(
                admin.getId());
        return adminOrganizationInfos.stream()
                .map(AdminInquireOrganizationResponse::from)
                .toList();
    }

    private Admin findAdminBy(final Long adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "해당 ID(id = " + adminId + ")인 admin을 찾을 수 없습니다."));
    }

    private void saveOrganizationCategory(
            final Set<String> categories,
            final Organization organization
    ) {
        final OrganizationCategories organizationCategories = OrganizationCategories.createAndConvert(
                categories, organization);
        organization.addOrganizationCategories(organizationCategories.getOrganizationCategories());
        organizationCategoryRepository.saveAll(organizationCategories.getOrganizationCategories());
    }
}
