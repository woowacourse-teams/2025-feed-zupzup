package feedzupzup.backend.group.dto;

import feedzupzup.backend.group.domain.Group;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "그룹 조회 응답")
public record GroupResponse(
        @Schema(description = "그룹 이름", example = "우아한테크코스")
        String groupName
) {
    public static GroupResponse from(final Group group) {
        return new GroupResponse(group.getName());
    }
}