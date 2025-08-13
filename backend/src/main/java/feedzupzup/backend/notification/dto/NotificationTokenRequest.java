package feedzupzup.backend.notification.dto;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.notification.domain.NotificationToken;

public record NotificationTokenRequest(
        String notificationToken
) {

    public NotificationToken toNotificationToken(final Admin admin) {
        return new NotificationToken(admin, notificationToken);
    }
}
