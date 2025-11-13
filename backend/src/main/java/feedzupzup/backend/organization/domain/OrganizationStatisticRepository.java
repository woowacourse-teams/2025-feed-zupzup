package feedzupzup.backend.organization.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrganizationStatisticRepository extends
        JpaRepository<OrganizationStatistic, Long> {

    @Query("""
            SELECT new feedzupzup.backend.organization.domain.FeedbackAmount(
                  s.feedbackAmount.feedbackTotalCount,
                  s.feedbackAmount.feedbackConfirmedCount,
                  s.feedbackAmount.feedbackWaitingCount)
            FROM OrganizationStatistic s
            WHERE s.organization.id = :organizationId
            """)
    FeedbackAmount findFeedbackAmountByOrganizationId(@Param("organizationId") Long organizationId);

    void deleteByOrganizationId(Long organizationId);

    void deleteAllByOrganizationIdIn(List<Long> organizationIds);

    @Modifying
    @Query("""
            UPDATE OrganizationStatistic s SET
            s.feedbackAmount.feedbackTotalCount = s.feedbackAmount.feedbackTotalCount + :totalAmount,
            s.feedbackAmount.feedbackConfirmedCount = s.feedbackAmount.feedbackConfirmedCount + :confirmedAmount,
            s.feedbackAmount.feedbackTotalCount = s.feedbackAmount.feedbackTotalCount + :waitingAmount
            WHERE s.organization.id = :organizationId
            """)
    void updateFeedbackAmount(
            @Param("organizationId") Long organizationId,
            long totalAmount,
            long confirmedAmount,
            long waitingAmount
    );

}
