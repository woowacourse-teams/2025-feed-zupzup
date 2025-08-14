package feedzupzup.backend.admin.domain;

import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.admin.domain.vo.Password;
import feedzupzup.backend.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @Column(nullable = false)
    private Password password;

    @Embedded
    @Column(nullable = false)
    private AdminName adminName;

    @Embedded
    @Column(nullable = false)
    private LoginId loginId;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean alertsOn = true;

    public Admin(@NonNull final LoginId loginId, @NonNull Password password, @NonNull final AdminName adminName) {
        this.loginId = loginId;
        this.password = password;
        this.adminName = adminName;
        this.alertsOn = true;
    }

    public Admin(@NonNull final LoginId loginId, @NonNull Password password, @NonNull final AdminName adminName, boolean alertsOn) {
        this.loginId = loginId;
        this.password = password;
        this.adminName = adminName;
        this.alertsOn = alertsOn;
    }

    public String getPasswordValue() {
        return password.getValue();
    }

    public void updateAlertsSetting(boolean alertsOn) {
        this.alertsOn = alertsOn;
    }
}
