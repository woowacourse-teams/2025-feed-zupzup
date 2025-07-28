package feedzupzup.backend.group.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import feedzupzup.backend.feedback.controller.E2EHelper;
import feedzupzup.backend.group.domain.Group;
import feedzupzup.backend.group.domain.GroupRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class GroupControllerE2ETest extends E2EHelper {

    @Autowired
    private GroupRepository groupRepository;

    @Test
    @DisplayName("그룹 ID로 그룹 이름을 성공적으로 조회한다")
    void get_group_name_success() {
        // given
        final Group group = new Group("우아한테크코스");
        final Group savedGroup = groupRepository.save(group);

        // when & then
        given()
                .log().all()
                .when()
                .get("/groups/{groupId}", savedGroup.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.groupName", equalTo("우아한테크코스"));
    }

    @Test
    @DisplayName("존재하지 않는 그룹 ID로 조회 시 404 에러를 반환한다")
    void get_group_name_not_found() {
        // given
        final Long nonExistentGroupId = 999L;

        // when & then
        given()
                .log().all()
                .when()
                .get("/groups/{groupId}", nonExistentGroupId)
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON);
    }
}