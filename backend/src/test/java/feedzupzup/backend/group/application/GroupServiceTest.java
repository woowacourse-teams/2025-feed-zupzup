package feedzupzup.backend.group.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import feedzupzup.backend.feedback.application.ServiceIntegrationHelper;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.group.domain.Group;
import feedzupzup.backend.group.domain.GroupRepository;
import feedzupzup.backend.group.dto.GroupResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GroupServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRepository groupRepository;

    @Nested
    @DisplayName("그룹 조회 테스트")
    class GetGroupByIdTest {

        @Test
        @DisplayName("그룹 ID로 그룹을 성공적으로 조회한다")
        void get_group_by_id_success() {
            // given
            final Group group = new Group("우아한테크코스");
            final Group savedGroup = groupRepository.save(group);

            // when
            final GroupResponse response = groupService.getGroupById(savedGroup.getId());

            // then
            assertThat(response.groupName()).isEqualTo("우아한테크코스");
        }

        @Test
        @DisplayName("존재하지 않는 그룹 ID로 조회 시 예외를 발생시킨다")
        void get_group_by_id_not_found() {
            // given
            final Long nonExistentGroupId = 999L;

            // when & then
            assertThatThrownBy(() -> groupService.getGroupById(nonExistentGroupId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("해당 ID(id = " + nonExistentGroupId + ")인 그룹을 찾을 수 없습니다.");
        }

        
    }
}