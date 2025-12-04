package feedzupzup.backend.sse.controller;


import feedzupzup.backend.auth.presentation.annotation.VisitedGuest;
import feedzupzup.backend.guest.dto.GuestInfo;
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

@Tag(name = "Sse", description = "Sse API")
public interface SseApi {

    @Operation(summary = "Sse 연결", description = "Http 연결을 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연결 성공", content = @Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE))
    })
    @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 조직"
    )
    @GetMapping(value = "/sse/subscribe/{organizationUuid}",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter subscribe(
            @Parameter(description = "단체 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("organizationUuid") final UUID organizationUuid,
            @Parameter(hidden = true) @VisitedGuest final GuestInfo guestInfo
    );
}
