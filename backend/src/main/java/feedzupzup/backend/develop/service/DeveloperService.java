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

        List<Feedback> allFeedbacks = feedbackRepository.findAll();
        log.info("총 피드백 수: {}", allFeedbacks.size());

        int processedCount = 0;
        int skippedCount = 0;

        for (Feedback feedback : allFeedbacks) {
            if (feedbackEmbeddingClusterRepository.existsByFeedback_Id(feedback.getId())) {
                skippedCount++;
                continue;
            }

            try {
                FeedbackEmbeddingCluster cluster = feedbackClusteringService.cluster(feedback.getId());
                feedbackClusteringService.createLabel(cluster.getId());
                processedCount++;
            } catch (Exception e) {
                log.error("피드백 ID {} 클러스터링 실패: {}", feedback.getId(), e.getMessage());
            }
        }

        log.info("배치 클러스터링 작업 완료. 처리: {}, 건너뜀: {}", processedCount, skippedCount);
    }
}
