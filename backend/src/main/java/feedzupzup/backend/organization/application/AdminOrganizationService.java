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
import feedzupzup.backend.organization.dto.request.UpdateOrganizationRequest;
import feedzupzup.backend.organization.dto.response.AdminCreateOrganizationResponse;
import feedzupzup.backend.organization.dto.response.AdminInquireOrganizationResponse;
import feedzupzup.backend.organization.dto.response.AdminUpdateOrganizationResponse;
import feedzupzup.backend.organization.event.OrganizationCreatedEvent;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.domain.OrganizerRole;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AdminCreateOrganizationResponse createOrganization(
            final CreateOrganizationRequest request,
            final Long adminId
    ) {
        final Organization organization = request.toOrganization();
        final Organization savedOrganization = organizationRepository.save(organization);

        final Set<String> categories = request.categories();

        saveOrganizationCategory(categories, savedOrganization);

        final Admin admin = findAdminBy(adminId);
        final Organizer organizer = new Organizer(
                organization,
                admin,
                OrganizerRole.OWNER
        );
        organizerRepository.save(organizer);

        eventPublisher.publishEvent(new OrganizationCreatedEvent(savedOrganization.getUuid()));

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
        final OrganizationCategories organizationCategories = OrganizationCategories.createOf(
                categories, organization);
        organization.addOrganizationCategories(organizationCategories.getOrganizationCategories());
        organizationCategoryRepository.saveAll(organizationCategories.getOrganizationCategories());
    }

    @Transactional
    public AdminUpdateOrganizationResponse updateOrganization(
            final UUID organizationUuid,
            final UpdateOrganizationRequest request,
            final Long adminId
    ) {

        final Organization organization = organizationRepository.findByUuid(organizationUuid)
                .orElseThrow(() -> new ResourceNotFoundException("해당 UUID를 가진 단체는 존재하지 않습니다."));

        // TODO : Exception 변경하기
        if (!organizerRepository.existsOrganizerByAdmin_IdAndOrganization_Id(adminId,
                organization.getId())) {
            throw new ResourceNotFoundException("해당 단체에 대한 접근 권한이 없습니다.");
        }

        final Set<String> categories = request.categories();
        final OrganizationCategories organizationCategories = OrganizationCategories.createOf(
                categories, organization);

        organization.updateOrganizationCategoriesAndName(
                organizationCategories.getOrganizationCategories(),
                request.organizationName()
        );
        return AdminUpdateOrganizationResponse.from(organization);

        // TODO: 연관관계 전이 때문에 반영이 안 될 것이다. 이를 수정해라.
    }
}
