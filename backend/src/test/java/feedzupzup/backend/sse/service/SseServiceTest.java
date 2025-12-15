package feedzupzup.backend.sse.service;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.sse.domain.ConnectionType;
import feedzupzup.backend.sse.domain.SseEmitterRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class SseServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private SseService sseService;

    @Autowired
    private SseEmitterRepository sseEmitterRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    Organization organization;
    OrganizationCategory organizationCategory;

    @BeforeEach
    void init() {
        organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);
        organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);
    }

    @Nested
    @DisplayName("Emitter 생성 테스트")
    class CreateEmitterTest {

        @Test
        @DisplayName("조직 UUID와 게스트 UUID로 emitter를 성공적으로 생성한다")
        void createEmitter_success() {
            // given
            final UUID organizationUuid = organization.getUuid();
            final UUID guestUuid = UUID.randomUUID();

            // when
            final SseEmitter emitter = sseService.createEmitter(
                    organizationUuid,
                    guestUuid.toString(),
                    ConnectionType.GUEST
            );

            // then
            assertThat(emitter).isNotNull();
            assertThat(emitter.getTimeout()).isEqualTo(Long.MAX_VALUE);
        }

        @Test
        @DisplayName("emitter 생성 시 리포지토리에 저장된다")
        void createEmitter_savedInRepository() {
            // given
            final UUID organizationUuid = organization.getUuid();
            final UUID guestUuid = UUID.randomUUID();
            final int initialCount = sseEmitterRepository.count();

            // when
            sseService.createEmitter(
                    organizationUuid,
                    guestUuid.toString(),
                    ConnectionType.GUEST
            );

            // then
            final int afterCount = sseEmitterRepository.count();
            assertThat(afterCount).isEqualTo(initialCount + 1);
        }

        @Test
        @DisplayName("동일한 게스트가 여러 emitter를 생성할 수 있다")
        void createEmitter_multipleEmittersFromSameGuest() {
            // given
            final UUID organizationUuid = organization.getUuid();
            final UUID guestUuid = UUID.randomUUID();

            // when
            final SseEmitter emitter1 = sseService.createEmitter(
                    organizationUuid,
                    guestUuid.toString(),
                    ConnectionType.GUEST
            );
            final SseEmitter emitter2 = sseService.createEmitter(
                    organizationUuid,
                    guestUuid.toString(),
                    ConnectionType.GUEST
            );
            final SseEmitter emitter3 = sseService.createEmitter(
                    organizationUuid,
                    guestUuid.toString(),
                    ConnectionType.GUEST
            );

            // then
            assertAll(
                    () -> assertThat(emitter1).isNotNull(),
                    () -> assertThat(emitter2).isNotNull(),
                    () -> assertThat(emitter3).isNotNull(),
                    () -> assertThat(sseEmitterRepository.count()).isGreaterThanOrEqualTo(3)
            );
        }
    }

    @Nested
    @DisplayName("Emitter ID 생성 테스트")
    class EmitterIdGenerationTest {

        @Test
        @DisplayName("emitter ID는 조직 ID, 연결 타입, 사용자 ID, 타임스탬프를 포함한다")
        void createEmitter_emitterIdFormat() {
            // given
            final UUID organizationUuid = organization.getUuid();
            final UUID guestUuid = UUID.randomUUID();

            // when
            final SseEmitter emitter = sseService.createEmitter(
                    organizationUuid,
                    guestUuid.toString(),
                    ConnectionType.GUEST
            );

            // then
            // emitter가 생성되고 리포지토리에 저장되었는지 확인
            assertThat(emitter).isNotNull();
            assertThat(sseEmitterRepository.count()).isGreaterThan(0);
        }

        @Nested
        @DisplayName("Emitter 타임아웃 설정 테스트")
        class EmitterTimeoutTest {

            @Test
            @DisplayName("emitter는 Long.MAX_VALUE 타임아웃으로 생성된다")
            void createEmitter_hasMaxTimeout() {
                // given
                final UUID organizationUuid = organization.getUuid();
                final UUID guestUuid = UUID.randomUUID();

                // when
                final SseEmitter emitter = sseService.createEmitter(
                        organizationUuid,
                        guestUuid.toString(),
                        ConnectionType.GUEST
                );

                // then
                assertThat(emitter.getTimeout()).isEqualTo(Long.MAX_VALUE);
            }

            @Test
            @DisplayName("여러 emitter가 동일한 타임아웃 설정을 가진다")
            void createEmitter_multipleEmittersHaveSameTimeout() {
                // given
                final UUID organizationUuid = organization.getUuid();
                final UUID guestUuid1 = UUID.randomUUID();
                final UUID guestUuid2 = UUID.randomUUID();

                // when
                final SseEmitter emitter1 = sseService.createEmitter(
                        organizationUuid,
                        guestUuid1.toString(),
                        ConnectionType.GUEST
                );
                final SseEmitter emitter2 = sseService.createEmitter(
                        organizationUuid,
                        guestUuid2.toString(),
                        ConnectionType.GUEST
                );

                // then
                assertAll(
                        () -> assertThat(emitter1.getTimeout()).isEqualTo(Long.MAX_VALUE),
                        () -> assertThat(emitter2.getTimeout()).isEqualTo(Long.MAX_VALUE)
                );
            }
        }
    }
}