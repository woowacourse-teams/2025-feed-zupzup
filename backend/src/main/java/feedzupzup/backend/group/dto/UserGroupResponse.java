package feedzupzup.backend.group.dto;

import feedzupzup.backend.group.domain.Group;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "그룹 조회 응답")
public record UserGroupResponse(
        @Schema(description = "그룹 이름", example = "우아한테크코스")
        String groupName
) {
    public static UserGroupResponse from(final Group group) {
        return new UserGroupResponse(group.getName());
    }
}
