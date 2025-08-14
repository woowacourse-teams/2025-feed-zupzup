package feedzupzup.backend.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class CurrentDateTime {

    private CurrentDateTime() {}

    private static final String TIME_ZONE_AREA = "Asia/Seoul";

    public static LocalDateTime create() {
        return LocalDateTime.now(ZoneId.of(TIME_ZONE_AREA)).truncatedTo(ChronoUnit.MICROS);
    }
}
