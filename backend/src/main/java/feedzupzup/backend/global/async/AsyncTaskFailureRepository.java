package feedzupzup.backend.global.async;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AsyncTaskFailureRepository extends JpaRepository<AsyncTaskFailure, Long> {

    List<AsyncTaskFailure> findAllByRetryable(boolean retryable);
}
