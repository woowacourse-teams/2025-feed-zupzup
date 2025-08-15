package feedzupzup.backend.organization.dto.response;

import feedzupzup.backend.organization.domain.AdminOrganizationInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "관리자가 속한 조직 조회 응답")
public record AdminInquireOrganizationResponse(
        @Schema(description = "조직 이름", example = "우테코")
        String name,

        @Schema(description = "대기중인 피드백 수", example = "5")
        long waitingCount,

        @Schema(description = "생성 날짜", example = "2025-07-13T10:30:00.000Z")
        LocalDateTime postedAt
) {

    public static AdminInquireOrganizationResponse from(final AdminOrganizationInfo info) {
        return new AdminInquireOrganizationResponse(
                info.organizationName(),
                info.waitingCount(),
                info.postedAt()
        );
    }
}
