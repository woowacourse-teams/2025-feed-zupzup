package feedzupzup.backend.admin.domain;

import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.EncodedPassword;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@SQLDelete(sql = "UPDATE admin SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Admin extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @Column(nullable = false)
    private EncodedPassword password;

    @Embedded
    @Column(nullable = false)
    private AdminName adminName;

    @Embedded
    @Column(nullable = false)
    private LoginId loginId;

    @Column(nullable = false)
    private boolean alertsOn;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Admin(@NonNull final LoginId loginId, @NonNull final EncodedPassword password, @NonNull final AdminName adminName) {
        this.loginId = loginId;
        this.password = password;
        this.adminName = adminName;
        this.alertsOn = false;
    }

    public String getPasswordValue() {
        return password.value();
    }

    public void updateAlertsSetting(boolean alertsOn) {
        this.alertsOn = alertsOn;
    }
}
