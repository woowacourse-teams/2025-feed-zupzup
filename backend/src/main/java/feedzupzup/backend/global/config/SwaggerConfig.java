package feedzupzup.backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSchemas("SuccessResponse", new Schema<>()
                                .type("object")
                                .description("성공 응답 래퍼")
                                .addProperty("data", new Schema<>().description("응답 데이터"))
                                .addProperty("status", new Schema<>().type("integer").description("상태 코드").example(200))
                                .addProperty("message", new Schema<>().type("string").description("상태 메시지").example("OK")))
                        .addResponses("BadRequest", new ApiResponse()
                                .description("잘못된 요청"))
                        .addResponses("Unauthorized", new ApiResponse()
                                .description("인증이 필요합니다"))
                        .addResponses("Forbidden", new ApiResponse()
                                .description("접근 권한이 없습니다"))
                        .addResponses("NotFound", new ApiResponse()
                                .description("요청한 리소스를 찾을 수 없습니다"))
                        .addResponses("Conflict", new ApiResponse()
                                .description("요청이 현재 서버 상태와 충돌합니다"))
                        .addResponses("NoContent", new ApiResponse()
                                .description("성공적으로 처리되었습니다")))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Feed ZupZup API")
                .description("피드줍줍 API 문서")
                .version("v1.0.0");
    }
}
