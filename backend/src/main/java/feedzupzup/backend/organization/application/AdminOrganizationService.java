package feedzupzup.backend.organization.application;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.category.application.OrganizationCategoryService;
import feedzupzup.backend.feedback.application.AdminFeedbackService;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.AdminOrganizationInfo;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.domain.OrganizationStatistic;
import feedzupzup.backend.organization.domain.OrganizationStatisticRepository;
import feedzupzup.backend.organization.dto.request.CreateOrganizationRequest;
import feedzupzup.backend.organization.dto.request.UpdateOrganizationRequest;
import feedzupzup.backend.organization.dto.response.AdminCreateOrganizationResponse;
import feedzupzup.backend.organization.dto.response.AdminInquireOrganizationResponse;
import feedzupzup.backend.organization.dto.response.AdminUpdateOrganizationResponse;
import feedzupzup.backend.organization.event.OrganizationCreatedEvent;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.domain.OrganizerRole;
import feedzupzup.backend.qr.service.QRService;
import java.util.List;
import java.util.Optional;
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
    private final OrganizationStatisticRepository organizationStatisticRepository;
    private final AdminRepository adminRepository;
    private final OrganizationCategoryService organizationCategoryService;
    private final ApplicationEventPublisher eventPublisher;
    private final AdminFeedbackService adminFeedbackService;
    private final QRService qrService;

    @Transactional
    public AdminCreateOrganizationResponse createOrganization(
            final CreateOrganizationRequest request,
            final Long adminId
    ) {
        final Organization organization = request.toOrganization();
        final Organization savedOrganization = organizationRepository.save(organization);

        final List<String> categories = request.categories();

        saveOrganizationCategory(categories, savedOrganization);

        final Admin admin = findAdminBy(adminId);
        final Organizer organizer = new Organizer(
                savedOrganization,
                admin,
                OrganizerRole.OWNER
        );
        organizerRepository.save(organizer);
        final OrganizationStatistic organizationStatistic = new OrganizationStatistic(
                savedOrganization);
        organizationStatisticRepository.save(organizationStatistic);

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
            final List<String> categories,
            final Organization organization
    ) {
        organizationCategoryService.saveAll(organization.getOrganizationCategories().getOrganizationCategories());
        organization.addOrganizationCategories(categories);
    }

    @Transactional
    public AdminUpdateOrganizationResponse updateOrganization(
            final UUID organizationUuid,
            final UpdateOrganizationRequest request
    ) {
        final Organization organization = organizationRepository.findByUuid(organizationUuid)
                .orElseThrow(() -> new ResourceNotFoundException("해당 UUID를 가진 단체는 존재하지 않습니다."));

        final List<String> categories = request.categories();
        organization.updateOrganizationCategoriesAndName(categories, request.organizationName());
        return AdminUpdateOrganizationResponse.from(organization);
    }

    @Transactional
    public void deleteAllByOrganizerIds(final List<Long> organizationIds) {
        organizationCategoryService.deleteAllByOrganizationIds(organizationIds);
        adminFeedbackService.deleteAllByOrganizationIds(organizationIds);
        qrService.deleteAllByOrganizationIds(organizationIds);
        organizationRepository.deleteAllById(organizationIds);
    }

    @Transactional
    public void deleteOrganization(final UUID organizationUuid) {
        final Optional<Organization> organizationOpt = organizationRepository.findByUuid(organizationUuid);
        if (organizationOpt.isEmpty()) {
            return;
        }
        final Organization organization = organizationOpt.get();
        final Long organizationId = organization.getId();

        organizerRepository.deleteAllByOrganization_Id(organizationId);
        adminFeedbackService.deleteByOrganizationId(organizationId);
        organizationCategoryService.deleteByOrganizationId(organizationId);
        qrService.deleteByOrganizationId(organizationId);
        organizationRepository.delete(organization);
    }
}
