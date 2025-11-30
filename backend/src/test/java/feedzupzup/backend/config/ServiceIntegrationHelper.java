package feedzupzup.backend.config;

/**
 * Service 통합 테스트용 헬퍼 클래스
 *
 * HTTP 레이어 없이 Service 레이어를 직접 테스트할 때 사용합니다.
 * IntegrationTestSupport를 상속받아 E2EHelper와 동일한 Context를 공유하여
 * 테스트 실행 시간을 크게 단축합니다.
 */
public abstract class ServiceIntegrationHelper extends IntegrationTestSupport {
    // IntegrationTestSupport의 setUp()만 사용
}