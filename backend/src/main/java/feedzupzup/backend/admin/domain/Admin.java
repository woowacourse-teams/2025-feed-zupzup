package feedzupzup.backend.admin.domain;

import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.Password;
import feedzupzup.backend.global.BaseTimeEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Password password;

    @Embedded
    private AdminName adminName;

    @Embedded
    private LoginId loginId;

    public Admin(final LoginId loginId, final Password password, final AdminName adminName) {
        this.loginId = loginId;
        this.password = password;
        this.adminName = adminName;
    }
}
