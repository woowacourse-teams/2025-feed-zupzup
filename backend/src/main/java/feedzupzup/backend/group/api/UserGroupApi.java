package feedzupzup.backend.group.api;

import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.group.dto.UserGroupResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Group", description = "그룹 API(권한: User)")
public interface UserGroupApi {

    @Operation(summary = "그룹 이름 조회", description = "그룹 ID로 그룹 이름을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/groups/{groupId}")
    SuccessResponse<UserGroupResponse> getGroupById(
            @Parameter(description = "그룹 ID", example = "1")
            @PathVariable("groupId") final Long groupId
    );
}
