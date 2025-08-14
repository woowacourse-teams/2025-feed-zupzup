package feedzupzup.backend.notification.event;

public record NotificationEvent(
        Long adminId,
        String title,
        String organizationName
) {
}
