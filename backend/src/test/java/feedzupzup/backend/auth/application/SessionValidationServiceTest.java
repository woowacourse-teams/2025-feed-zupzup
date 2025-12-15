package feedzupzup.backend.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import feedzupzup.backend.admin.domain.AdminRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionValidationServiceTest {

    @InjectMocks
    private SessionValidationService sessionValidationService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private ActiveSessionStore activeSessionStore;

    @Test
    @DisplayName("활성 세션이 있고 알림이 활성화된 관리자 ID 목록을 반환한다")
    void getValidSessionAdminIds_WithActiveSessionsAndAlertsEnabled_ReturnsValidIds() {
        // given
        List<Long> adminIds = List.of(1L, 2L, 3L, 4L);
        List<Long> activeSessionAdminIds = List.of(1L, 3L, 4L);  // 2L은 세션 없음
        List<Long> alertsEnabledAdminIds = List.of(1L, 4L);  // 3L은 알림 비활성화

        given(activeSessionStore.hasActiveSession(1L)).willReturn(true);
        given(activeSessionStore.hasActiveSession(2L)).willReturn(false);
        given(activeSessionStore.hasActiveSession(3L)).willReturn(true);
        given(activeSessionStore.hasActiveSession(4L)).willReturn(true);
        given(adminRepository.findAlertsEnabledAdminIds(activeSessionAdminIds))
                .willReturn(alertsEnabledAdminIds);

        // when
        List<Long> result = sessionValidationService.getValidSessionAdminIds(adminIds);

        // then
        assertThat(result).containsExactly(1L, 4L);
        verify(activeSessionStore).hasActiveSession(1L);
        verify(activeSessionStore).hasActiveSession(2L);
        verify(activeSessionStore).hasActiveSession(3L);
        verify(activeSessionStore).hasActiveSession(4L);
        verify(adminRepository).findAlertsEnabledAdminIds(activeSessionAdminIds);
    }

    @Test
    @DisplayName("모든 관리자가 활성 세션이 없으면 빈 리스트를 반환한다")
    void getValidSessionAdminIds_NoActiveSessions_ReturnsEmptyList() {
        // given
        List<Long> adminIds = List.of(1L, 2L, 3L);

        given(activeSessionStore.hasActiveSession(1L)).willReturn(false);
        given(activeSessionStore.hasActiveSession(2L)).willReturn(false);
        given(activeSessionStore.hasActiveSession(3L)).willReturn(false);

        // when
        List<Long> result = sessionValidationService.getValidSessionAdminIds(adminIds);

        // then
        assertThat(result).isEmpty();
        verify(activeSessionStore).hasActiveSession(1L);
        verify(activeSessionStore).hasActiveSession(2L);
        verify(activeSessionStore).hasActiveSession(3L);
        verifyNoInteractions(adminRepository);
    }

    @Test
    @DisplayName("활성 세션은 있지만 알림이 활성화된 관리자가 없으면 빈 리스트를 반환한다")
    void getValidSessionAdminIds_ActiveSessionsButNoAlertsEnabled_ReturnsEmptyList() {
        // given
        List<Long> adminIds = List.of(1L, 2L);
        List<Long> activeSessionAdminIds = List.of(1L, 2L);

        given(activeSessionStore.hasActiveSession(1L)).willReturn(true);
        given(activeSessionStore.hasActiveSession(2L)).willReturn(true);
        given(adminRepository.findAlertsEnabledAdminIds(activeSessionAdminIds))
                .willReturn(List.of());

        // when
        List<Long> result = sessionValidationService.getValidSessionAdminIds(adminIds);

        // then
        assertThat(result).isEmpty();
        verify(adminRepository).findAlertsEnabledAdminIds(activeSessionAdminIds);
    }

    @Test
    @DisplayName("빈 관리자 ID 목록을 받으면 빈 리스트를 반환한다")
    void getValidSessionAdminIds_EmptyInput_ReturnsEmptyList() {
        // given
        List<Long> emptyAdminIds = List.of();

        // when
        List<Long> result = sessionValidationService.getValidSessionAdminIds(emptyAdminIds);

        // then
        assertThat(result).isEmpty();
        verifyNoInteractions(activeSessionStore);
        verifyNoInteractions(adminRepository);
    }

    @Test
    @DisplayName("모든 관리자가 활성 세션과 알림이 활성화되어 있으면 모든 ID를 반환한다")
    void getValidSessionAdminIds_AllValid_ReturnsAllIds() {
        // given
        List<Long> adminIds = List.of(1L, 2L, 3L);

        given(activeSessionStore.hasActiveSession(1L)).willReturn(true);
        given(activeSessionStore.hasActiveSession(2L)).willReturn(true);
        given(activeSessionStore.hasActiveSession(3L)).willReturn(true);
        given(adminRepository.findAlertsEnabledAdminIds(adminIds))
                .willReturn(adminIds);

        // when
        List<Long> result = sessionValidationService.getValidSessionAdminIds(adminIds);

        // then
        assertThat(result).containsExactly(1L, 2L, 3L);
    }
}
