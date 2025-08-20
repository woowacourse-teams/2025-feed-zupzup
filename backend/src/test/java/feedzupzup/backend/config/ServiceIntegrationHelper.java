package feedzupzup.backend.config;


import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestcontainersTest
public abstract class ServiceIntegrationHelper {

    @Autowired
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        dataInitializer.deleteAll();
    }

}
