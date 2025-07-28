package feedzupzup.backend.group.controller;

import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.group.api.UserGroupApi;
import feedzupzup.backend.group.application.UserGroupService;
import feedzupzup.backend.group.dto.UserGroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserGroupController implements UserGroupApi {

    private final UserGroupService userGroupService;

    @Override
    public SuccessResponse<UserGroupResponse> getGroupById(final Long groupId) {
        final UserGroupResponse response = userGroupService.getGroupById(groupId);
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
