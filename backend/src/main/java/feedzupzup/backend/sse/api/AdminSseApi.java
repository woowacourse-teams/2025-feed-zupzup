package feedzupzup.backend.sse.api;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.annotation.AdminAuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Admin Sse", description = "Admin Sse API")
public interface AdminSseApi {

    @Operation(summary = "Admin SSE 연결", description = "관리자 페이지에서 SSE 연결을 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연결 성공", content = @Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE))
    })
    @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 조직"
    )
    @GetMapping(value = "/admin/sse/subscribe/{organizationUuid}",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter subscribeAdmin(
            @Parameter(description = "단체 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("organizationUuid") final UUID organizationUuid,
            @Parameter(hidden = true) @AdminAuthenticationPrincipal final AdminSession adminSession
    );
}
