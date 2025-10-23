package feedzupzup.backend.develop.service;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.vo.EncodedPassword;
import feedzupzup.backend.admin.domain.vo.Password;
import feedzupzup.backend.auth.application.PasswordEncoder;
import feedzupzup.backend.develop.dto.UpdateAdminPasswordRequest;
import feedzupzup.backend.feedback.application.FeedbackClusteringService;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingCluster;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingClusterRepository;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeveloperService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganizationRepository organizationRepository;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackEmbeddingClusterRepository feedbackEmbeddingClusterRepository;
    private final FeedbackClusteringService feedbackClusteringService;

    @Transactional
    public void changePassword(final UpdateAdminPasswordRequest request) {
        Admin admin = adminRepository.findByLoginId_Value(request.loginId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 adminId 입니다."));
        final EncodedPassword newPassword = passwordEncoder.encode(new Password(request.changePasswordValue()));
        admin.changePassword(newPassword);
    }

    @Transactional
    public void batchClustering() {
        log.info("배치 클러스터링 작업 시작");

        final int batchSize = 50;
        final int organizationBatchSize = 20;

        int currentPage = 0;
        Page<Organization> organizationPage;

        do {
            Pageable pageable = PageRequest.of(currentPage, organizationBatchSize);
            organizationPage = organizationRepository.findAll(pageable);

            log.info("조직 배치 처리 중: page {}/{}", currentPage + 1, organizationPage.getTotalPages());

            for (Organization organization : organizationPage.getContent()) {
                processOrganizationClustering(organization.getId(), batchSize);
            }

            currentPage++;
        } while (currentPage < organizationPage.getTotalPages());

        log.info("배치 클러스터링 작업 완료. 총 처리된 조직 수: {}", organizationPage.getTotalElements());
    }

    private void processOrganizationClustering(final Long organizationId, final int batchSize) {
        log.info("조직 ID {} 클러스터링 처리 시작", organizationId);

        int page = 0;
        int processedFeedbacks = 0;
        
        while (true) {
            final Pageable pageable = PageRequest.of(page, batchSize);
            final List<Feedback> feedbacks = findFeedbacksByOrganization(organizationId, pageable);
            if (feedbacks.isEmpty()) {
                break;
            }
            
            log.info("조직 ID {} 피드백 배치 처리 중 (페이지 {}): {}개", organizationId, page, feedbacks.size());
            
            for (Feedback feedback : feedbacks) {
                // 이미 클러스터링된 피드백은 건너뛰기
                if (feedbackEmbeddingClusterRepository.existsByFeedback_Id(feedback.getId())) {
                    log.info("이미 처리된 피드백 ID : {}", feedback.getId());
                    continue;
                }
                
                try {
                    FeedbackEmbeddingCluster cluster = feedbackClusteringService.cluster(feedback.getId());
                    feedbackClusteringService.createLabel(cluster.getId());
                    processedFeedbacks++;
                } catch (Exception e) {
                    log.error("피드백 ID {} 클러스터링 실패: {}", feedback.getId(), e.getMessage());
                }
            }
            
            page++;
        }

        log.info("조직 ID {} 클러스터링 완료. 처리된 총 피드백 수: {}", organizationId, processedFeedbacks);
    }

    private List<Feedback> findFeedbacksByOrganization(final Long organizationId, final Pageable pageable) {
        return feedbackRepository.findByOrganization_Id(organizationId, pageable).getContent().stream()
                .filter(feedback -> feedback.getOrganization().getId().equals(organizationId))
                .toList();
    }
}
