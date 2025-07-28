package feedzupzup.backend.group.application;

import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.group.domain.Group;
import feedzupzup.backend.group.domain.GroupRepository;
import feedzupzup.backend.group.dto.UserGroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserGroupService {

    private final GroupRepository groupRepository;

    public UserGroupResponse getGroupById(final Long groupId) {
        final Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID(id = " + groupId + ")인 그룹을 찾을 수 없습니다."));
        
        return UserGroupResponse.from(group);
    }
}
