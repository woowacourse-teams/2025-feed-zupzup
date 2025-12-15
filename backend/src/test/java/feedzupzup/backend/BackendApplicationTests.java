package feedzupzup.backend;

import feedzupzup.backend.config.IntegrationTestSupport;
import org.junit.jupiter.api.Test;

/**
 * Spring Boot Application Context 로딩 테스트
 *
 * IntegrationTestSupport를 상속받아 다른 통합 테스트들과 Context를 공유합니다.
 */
class BackendApplicationTests extends IntegrationTestSupport {

    @Test
    void contextLoads() {
        // Context가 정상적으로 로드되는지만 확인
    }

}