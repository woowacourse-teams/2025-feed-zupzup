package feedzupzup.backend.organization.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Set;

@Schema(description = "단체 수정 요청")
public record UpdateOrganizationRequest(
        @Schema(description = "단체 이름", example = "우아한테크코스")
        String organizationName,

        @Schema(description = "카테고리 리스트", example = "[\"건의\", \"신고\"]")
        List<String> categories
) {

}
