package feedzupzup.backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * 통합 테스트의 최상위 베이스 클래스
 *
 * 모든 통합 테스트(E2E, Service)가 동일한 Spring Application Context를 공유하여
 * Context 재로딩을 방지하고 테스트 실행 시간을 크게 단축합니다.
 *
 * Context 캐싱 조건:
 * - 동일한 @SpringBootTest 설정
 * - 동일한 @TestcontainersTest (MySQL 컨테이너)
 * - 동일한 webEnvironment
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestcontainersTest
public abstract class IntegrationTestSupport {

    @Autowired
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        dataInitializer.deleteAll();
    }
}