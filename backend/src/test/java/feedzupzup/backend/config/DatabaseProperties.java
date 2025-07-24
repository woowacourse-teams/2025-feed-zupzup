package feedzupzup.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "test-datasource")
public record DatabaseProperties(
        String version,
        String databaseName,
        String username,
        String password
) {

}
