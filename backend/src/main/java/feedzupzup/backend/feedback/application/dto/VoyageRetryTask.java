package feedzupzup.backend.feedback.application.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Voyage AI 재시도 작업
 *
 * Redis(Rqueue)로 전송되는 메시지
 * 재시도 관리는 100% Rqueue가 담당
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoyageRetryTask implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long feedbackId;

    public static VoyageRetryTask of(final Long feedbackId) {
        return new VoyageRetryTask(feedbackId);
    }
}