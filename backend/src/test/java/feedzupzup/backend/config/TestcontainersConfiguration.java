package feedzupzup.backend.config;

import feedzupzup.backend.auth.application.PasswordEncoder;
import feedzupzup.backend.auth.fake.FakePasswordEncoder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
@EnableConfigurationProperties(DatabaseProperties.class)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection(name = "redis")
    static GenericContainer<?> redisContainer() {
        return new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379)
            .withReuse(true);
    }

    @Bean
    PasswordEncoder fakePasswordEncoder() {
        return new FakePasswordEncoder();
    }

    @Bean
    @ServiceConnection
    static MySQLContainer<?> mysqlContainer(final DatabaseProperties databaseProperties) {
        return new MySQLContainer<>(MySQLContainer.NAME + ":" + databaseProperties.version())
            .withReuse(true)
            .withDatabaseName(databaseProperties.databaseName())
            .withUsername(databaseProperties.username())
            .withPassword(databaseProperties.password());
    }
}
