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
            s.feedbackAmount.feedbackTotalCount = s.feedbackAmount.feedbackTotalCount + :totalCount,
            s.feedbackAmount.feedbackConfirmedCount = s.feedbackAmount.feedbackConfirmedCount + :confirmedCount,
            s.feedbackAmount.feedbackWaitingCount = s.feedbackAmount.feedbackWaitingCount + :waitingCount
            WHERE s.organization.id = :organizationId
            """)
    void updateOrganizationStatisticCounts(
            @Param("organizationId") Long organizationId,
            long totalCount,
            long confirmedCount,
            long waitingCount
    );

}
