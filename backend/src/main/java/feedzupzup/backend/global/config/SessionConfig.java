package feedzupzup.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@Configuration
@EnableJdbcHttpSession(
        maxInactiveIntervalInSeconds = 14 * 24 * 60 * 60  // 2주
)
public class SessionConfig {
}