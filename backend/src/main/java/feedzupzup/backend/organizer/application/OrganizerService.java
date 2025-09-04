package feedzupzup.backend.organizer.application;

import feedzupzup.backend.organization.application.AdminOrganizationService;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrganizerService {

    private final OrganizerRepository organizerRepository;
    private final AdminOrganizationService adminOrganizationService;

    @Transactional
    public void deleteAllByAdminId(final Long adminId) {
        List<Organizer> organizers = organizerRepository.findAllFetchedByAdminId(adminId);
        List<Long> organizationIds = organizers.stream()
                .map(organizer -> organizer.getOrganization().getId())
                .toList();
        organizerRepository.deleteAllByAdmin_Id(adminId);
        adminOrganizationService.deleteAllByOrganizerIds(organizationIds);
    }
}
