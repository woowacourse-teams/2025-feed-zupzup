package feedzupzup.backend.notification.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.SendResponse;
import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.EncodedPassword;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.notification.domain.NotificationToken;
import feedzupzup.backend.notification.domain.NotificationTokenRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class FcmErrorHandlerTest extends ServiceIntegrationHelper {

    @Autowired
    private FcmErrorHandler fcmErrorHandler;
    
    @Autowired
    private NotificationTokenRepository notificationTokenRepository;
    
    @Autowired
    private AdminRepository adminRepository;

    @Test
    @Transactional
    @DisplayName("UNREGISTERED 에러는 토큰을 삭제한다")
    void handleFailures_DeletesInvalidTokens() {
        // given
        Admin admin = createAndSaveAdmin("testAdmin", "testLogin");
        NotificationToken token = new NotificationToken(admin, "invalid-token");
        notificationTokenRepository.save(token);
        
        BatchResponse batchResponse = mock(BatchResponse.class);
        SendResponse failResponse = mock(SendResponse.class);
        FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
        
        given(batchResponse.getResponses()).willReturn(List.of(failResponse));
        given(failResponse.isSuccessful()).willReturn(false);
        given(failResponse.getException()).willReturn(exception);
        given(exception.getMessagingErrorCode()).willReturn(MessagingErrorCode.UNREGISTERED);
        
        List<Long> adminIds = List.of(admin.getId());

        // when
        fcmErrorHandler.handleFailures(batchResponse, adminIds);

        // then
        assertThat(notificationTokenRepository.findByAdmin_Id(admin.getId())).isEmpty();
    }

    @Test
    @DisplayName("성공한 경우 토큰을 삭제하지 않는다")
    void handleFailures_DoesNotDeleteValidTokens() {
        // given
        Admin admin = createAndSaveAdmin("testAdmin2", "testLogin2");
        NotificationToken token = new NotificationToken(admin, "valid-token");
        notificationTokenRepository.save(token);
        
        BatchResponse batchResponse = mock(BatchResponse.class);
        SendResponse successResponse = mock(SendResponse.class);
        
        given(batchResponse.getResponses()).willReturn(List.of(successResponse));
        given(successResponse.isSuccessful()).willReturn(true);
        
        List<Long> adminIds = List.of(admin.getId());

        // when
        fcmErrorHandler.handleFailures(batchResponse, adminIds);

        // then
        assertThat(notificationTokenRepository.findByAdmin_Id(admin.getId())).isPresent();
    }

    private Admin createAndSaveAdmin(String adminName, String loginId) {
        Admin admin = new Admin(
                new LoginId(loginId),
                new EncodedPassword("password123"),
                new AdminName(adminName)
        );
        return adminRepository.save(admin);
    }
}
