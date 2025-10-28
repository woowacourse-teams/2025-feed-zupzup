package feedzupzup.backend.notification.domain;

public record NotificationPayload(
        Long adminId,
        String title,
        String organizationName) {
}
