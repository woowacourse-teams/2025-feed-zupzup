package feedzupzup.backend.config;

import feedzupzup.backend.global.config.JpaAuditingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@TestcontainersTest
@Import({DataInitializer.class, JpaAuditingConfig.class})
public abstract class RepositoryHelper {

    @Autowired
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        dataInitializer.deleteAll();
    }
}
