package feedzupzup.backend.group.controller;

import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.group.api.GroupApi;
import feedzupzup.backend.group.application.GroupService;
import feedzupzup.backend.group.dto.GroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GroupController implements GroupApi {

    private final GroupService groupService;

    @Override
    public SuccessResponse<GroupResponse> getGroupById(final Long groupId) {
        final GroupResponse response = groupService.getGroupById(groupId);
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
