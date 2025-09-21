package feedzupzup.backend.organization.dto.request;

import feedzupzup.backend.organization.domain.CheeringCount;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "응원하기 요청")
public record CheeringRequest(
        @Schema(description = "응원한 개수", example = "200")
        int cheeringCount
) {

        public CheeringCount toCheeringCount() {
                return new CheeringCount(cheeringCount);
        }
}
