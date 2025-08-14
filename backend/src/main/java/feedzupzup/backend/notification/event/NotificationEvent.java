package feedzupzup.backend.notification.event;

import java.util.List;

public record NotificationEvent(
        List<Long> adminIds,
        String title,
        String organizationName
) {
}
