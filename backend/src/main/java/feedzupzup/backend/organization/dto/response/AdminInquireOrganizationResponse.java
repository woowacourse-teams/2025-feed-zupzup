package feedzupzup.backend.organization.dto.response;

import feedzupzup.backend.organization.domain.AdminOrganizationInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "관리자가 속한 단체 조회 응답")
public record AdminInquireOrganizationResponse(
        @Schema(description = "단체 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID uuid,

        @Schema(description = "단체 이름", example = "우테코")
        String name,

        @Schema(description = "완료된 피드백 수", example = "5")
        long confirmedCount,

        @Schema(description = "대기중인 피드백 수", example = "5")
        long waitingCount,

        @Schema(description = "생성 날짜", example = "2025-07-13T10:30:00.000Z")
        LocalDateTime postedAt
) {

    public static AdminInquireOrganizationResponse from(final AdminOrganizationInfo info) {
        return new AdminInquireOrganizationResponse(
                info.organizationUuid(),
                info.organizationName(),
                info.confirmedCount(),
                info.waitingCount(),
                info.postedAt()
        );
    }
}
