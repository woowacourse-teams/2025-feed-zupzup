package feedzupzup.backend.feedback.controller;

import feedzupzup.backend.config.DataInitializer;
import feedzupzup.backend.config.TestcontainersTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestcontainersTest
public abstract class E2EHelper {

    @LocalServerPort
    private int port;

    @Autowired
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        dataInitializer.deleteAll();
        RestAssured.port = port;
        RestAssured.basePath = "/";
    }
}
