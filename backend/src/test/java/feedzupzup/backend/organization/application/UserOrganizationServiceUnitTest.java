package feedzupzup.backend.organization.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.dto.request.CheeringRequest;
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
class UserOrganizationServiceUnitTest {

    @InjectMocks
    private UserOrganizationService userOrganizationService;

    @Mock
    private OrganizationRepository organizationRepository;

    @Nested
    @DisplayName("단체 조회 예외 테스트")
    class GetOrganizationExceptionTest {

        @Test
        @DisplayName("존재하지 않는 단체 ID로 조회 시 예외를 발생시킨다")
        void get_organization_by_id_not_found() {
            // given
            UUID nonExistentOrganizationId = UUID.randomUUID();

            given(organizationRepository.findByUuid(nonExistentOrganizationId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userOrganizationService.getOrganizationByUuid(nonExistentOrganizationId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("해당 ID(id = " + nonExistentOrganizationId + ")인 단체를 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("응원하기 예외 테스트")
    class CheerExceptionTest {

        @Test
        @DisplayName("존재하지 않는 단체에 응원 시 예외를 발생시킨다")
        void cheer_organization_not_found() {
            // given
            UUID nonExistentOrganizationId = UUID.randomUUID();
            CheeringRequest request = new CheeringRequest(100);

            given(organizationRepository.findByUuid(nonExistentOrganizationId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userOrganizationService.cheer(request, nonExistentOrganizationId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
