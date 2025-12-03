package feedzupzup.backend.config;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * E2E (End-to-End) 테스트용 헬퍼 클래스
 *
 * RestAssured를 사용한 HTTP 요청 테스트를 위한 설정을 제공합니다.
 * IntegrationTestSupport를 상속받아 Context를 공유합니다.
 */
public abstract class E2EHelper extends IntegrationTestSupport {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
    }
}