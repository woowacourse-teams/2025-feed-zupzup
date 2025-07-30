package feedzupzup.backend.organization.dto.response;

import feedzupzup.backend.organization.domain.CheeringCount;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "응원하기 응답")
public record CheeringResponse(
        @Schema(description = "단체의 응원 총 개수", example = "200")
        int cheeringTotalCount
) {

        public static CheeringResponse from(final CheeringCount cheeringCount) {
                return new CheeringResponse(cheeringCount.getValue());
        }
}
