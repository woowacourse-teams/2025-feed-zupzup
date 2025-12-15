package feedzupzup.backend.organization.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.category.application.OrganizationCategoryService;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.domain.OrganizationStatisticRepository;
import feedzupzup.backend.organization.dto.request.CreateOrganizationRequest;
import feedzupzup.backend.organization.dto.request.UpdateOrganizationRequest;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.qr.service.QRService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminOrganizationServiceUnitTest {

    @InjectMocks
    private AdminOrganizationService adminOrganizationService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private OrganizerRepository organizerRepository;

    @Mock
    private OrganizationCategoryService organizationCategoryService;

    @Mock
    private OrganizationStatisticRepository organizationStatisticRepository;

    @Mock
    private QRService qrService;

    @Nested
    @DisplayName("조직 생성 예외 테스트")
    class CreateOrganizationExceptionTest {

        @Test
        @DisplayName("존재하지 않는 admin이 조직을 생성하려고 한다면, 예외가 발생해야 한다")
        void invalid_admin_request_then_throw_exception() {
            // given
            CreateOrganizationRequest request = new CreateOrganizationRequest(
                    "우테코",
                    List.of("건의", "신고")
            );
            Long invalidAdminId = 999L;

            // organizationRepository.save()가 먼저 호출되므로 Mock 설정 필요
            given(organizationRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));
            given(adminRepository.findById(invalidAdminId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminOrganizationService.createOrganization(request, invalidAdminId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("admin을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("조직 수정 예외 테스트")
    class UpdateOrganizationExceptionTest {

        @Test
        @DisplayName("존재하지 않는 조직을 수정하려고 하면 예외가 발생한다")
        void update_not_found_organization_then_throw_exception() {
            // given
            UUID nonExistentUuid = UUID.randomUUID();
            UpdateOrganizationRequest request = new UpdateOrganizationRequest(
                    "우테코코코",
                    List.of("기타", "칭찬", "정보공유")
            );

            given(organizationRepository.findByUuid(nonExistentUuid)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminOrganizationService.updateOrganization(nonExistentUuid, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }


    @Nested
    @DisplayName("조직 조회 예외 테스트")
    class GetOrganizationExceptionTest {

        @Test
        @DisplayName("존재하지 않는 admin의 조직 조회 시 예외가 발생한다")
        void get_organizations_with_invalid_admin_then_throw_exception() {
            // given
            Long invalidAdminId = 999L;

            given(adminRepository.findById(invalidAdminId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminOrganizationService.getOrganizationsInfo(invalidAdminId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}