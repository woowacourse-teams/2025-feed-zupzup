package feedzupzup.backend.admin.domain;

import feedzupzup.backend.admin.domain.vo.LoginId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    Optional<Admin> findById(final Long adminId);
    
    Optional<Admin> findByLoginId(final LoginId loginId);
    
    boolean existsByLoginId(final LoginId loginId);
}
