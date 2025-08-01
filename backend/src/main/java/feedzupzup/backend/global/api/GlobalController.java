package feedzupzup.backend.global.api;

import feedzupzup.backend.global.response.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class GlobalController {

    @GetMapping("/health-check")
    public SuccessResponse<String> healthCheck() {
        log.info("Health check successfully");
        return SuccessResponse.success(HttpStatus.OK, "Health check successfully");
    }
}
