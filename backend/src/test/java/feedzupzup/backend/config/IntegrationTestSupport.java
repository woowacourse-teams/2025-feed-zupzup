package feedzupzup.backend.config;

import feedzupzup.backend.feedback.application.FeedbackClusteringService;
import feedzupzup.backend.feedback.domain.ClusterLabelGenerator;
import feedzupzup.backend.feedback.domain.EmbeddingExtractor;
import feedzupzup.backend.global.async.AsyncFailureAlertService;
import feedzupzup.backend.global.async.AsyncTaskFailureService;
import feedzupzup.backend.qr.service.QRCodeGenerator;
import feedzupzup.backend.s3.config.LocalStackS3TestConfig;
import feedzupzup.backend.s3.service.S3PresignedDownloadService;
import feedzupzup.backend.s3.service.S3UploadService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 * 통합 테스트의 최상위 베이스 클래스
 *
 * 모든 통합 테스트(E2E, Service)가 동일한 Spring Application Context를 공유하여
 * Context 재로딩을 방지하고 테스트 실행 시간을 크게 단축합니다.
 *
 * Context 캐싱 조건:
 * - 동일한 @SpringBootTest 설정
 * - 동일한 @TestcontainersTest (MySQL 컨테이너)
 * - 동일한 @Import (LocalStackS3TestConfig - S3 테스트 통합)
 * - 동일한 webEnvironment
 * - 동일한 @MockitoBean 구성
 *
 * 공통 Mock Bean:
 * - QR/S3 관련: QRCodeGenerator, S3UploadService, S3PresignedDownloadService
 * - AI 관련: EmbeddingExtractor, ClusterLabelGenerator, FeedbackClusteringService
 * - Async 관련: AsyncFailureAlertService, AsyncTaskFailureService
 *
 * LocalStack 통합:
 * - LocalStackS3TestConfig를 통해 S3 테스트용 LocalStack 컨테이너 공유
 * - S3ServiceIntegrationHelper는 별도 @LocalStackS3Test 없이 사용 가능
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestcontainersTest
@Import(LocalStackS3TestConfig.class)
@TestExecutionListeners(
        value = {TestExecutionTimeListener.class, DependencyInjectionTestExecutionListener.class},
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public abstract class IntegrationTestSupport {

    @MockitoBean
    protected QRCodeGenerator qrCodeGenerator;

    @MockitoBean
    protected AsyncFailureAlertService asyncFailureAlertService;

    @MockitoBean
    protected EmbeddingExtractor embeddingExtractor;

    @MockitoBean
    protected ClusterLabelGenerator clusterLabelGenerator;

    @MockitoSpyBean
    protected FeedbackClusteringService feedbackClusteringService;

    @MockitoSpyBean
    protected S3UploadService s3UploadService;

    @MockitoSpyBean
    protected S3PresignedDownloadService s3PresignedDownloadService;

    @MockitoSpyBean
    protected AsyncTaskFailureService asyncTaskFailureService;

    @Autowired
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        dataInitializer.deleteAll();
    }
}
