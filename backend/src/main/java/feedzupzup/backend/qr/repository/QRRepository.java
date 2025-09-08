package feedzupzup.backend.qr.repository;

import feedzupzup.backend.qr.domain.QR;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QRRepository extends JpaRepository<QR, Long> {

    Optional<QR> findByOrganizationId(Long organizationId);

    boolean existsByOrganizationId(Long id);

    void deleteAllByOrganizationIdIn(List<Long> organizationIds);
    
    void deleteByOrganizationId(Long organizationId);
}
