package feedzupzup.backend.feedback.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmbeddingClusterRepository extends JpaRepository<EmbeddingCluster, Long> {

}
