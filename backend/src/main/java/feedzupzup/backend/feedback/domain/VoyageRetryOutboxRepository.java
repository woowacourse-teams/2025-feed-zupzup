package feedzupzup.backend.feedback.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoyageRetryOutboxRepository extends JpaRepository<VoyageRetryOutbox, Long> {

    Optional<VoyageRetryOutbox> findByFeedbackId(Long feedbackId);

    void deleteByFeedbackId(Long feedbackId);
}
