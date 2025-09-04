package feedzupzup.backend.organizer.domain;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.global.BaseTimeEntity;
import feedzupzup.backend.organization.domain.Organization;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Organizer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Organization organization;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Admin admin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizerRole role;

    public boolean isAlertsOn() {
        return admin.isAlertsOn();
    }

    public boolean isLoggedIn() {
        return admin.isLoggedIn();
    }

    public Long getAdminId() {
        return admin.getId();
    }

    public Organizer(
            final @NonNull Organization organization,
            final @NonNull Admin admin,
            final @NonNull OrganizerRole role
    ) {
        this.organization = organization;
        this.admin = admin;
        this.role = role;
    }
}
