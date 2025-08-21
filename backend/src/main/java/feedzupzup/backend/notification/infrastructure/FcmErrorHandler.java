package feedzupzup.backend.notification.infrastructure;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.SendResponse;
import feedzupzup.backend.notification.domain.NotificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class FcmErrorHandler {

    private static final Set<MessagingErrorCode> DELETABLE_ERRORS = Set.of(
            MessagingErrorCode.UNREGISTERED,
            MessagingErrorCode.INVALID_ARGUMENT,
            MessagingErrorCode.SENDER_ID_MISMATCH
    );
    
    private static final Set<MessagingErrorCode> RETRYABLE_ERRORS = Set.of(
            MessagingErrorCode.UNAVAILABLE,
            MessagingErrorCode.QUOTA_EXCEEDED,
            MessagingErrorCode.INTERNAL
    );

    private final NotificationTokenRepository notificationTokenRepository;

    @Transactional
    public void handleFailures(final BatchResponse response, final List<Long> adminIds) {
        List<SendResponse> responses = response.getResponses();
        List<Long> deletableAdminIds = new ArrayList<>();

        for (int i = 0; i < responses.size(); i++) {
            SendResponse sendResponse = responses.get(i);
            if (sendResponse.isSuccessful()) {
                continue;
            }

            Long adminId = adminIds.get(i);
            FirebaseMessagingException exception = sendResponse.getException();
            MessagingErrorCode errorCode = exception.getMessagingErrorCode();

            if (DELETABLE_ERRORS.contains(errorCode)) {
                deletableAdminIds.add(adminId);
            } else if (RETRYABLE_ERRORS.contains(errorCode)) {
                log.warn("재시도 가능한 에러 - adminId: {}, error: {}", adminId, errorCode);
                // TODO: 추후 지수 백오프를 이용한 재시도 로직 구현 고려
            } else {
                log.warn("처리되지 않은 FCM 에러 - adminId: {}, error: {}", adminId, errorCode);
            }
        }

        if (!deletableAdminIds.isEmpty()) {
            log.info("토큰 삭제 adminId : {}", deletableAdminIds);
            notificationTokenRepository.deleteAllByAdminIdIn(deletableAdminIds);
        }
    }
}
