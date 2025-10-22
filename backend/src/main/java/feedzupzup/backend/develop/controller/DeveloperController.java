package feedzupzup.backend.develop.controller;

import feedzupzup.backend.auth.exception.AuthException.UnauthorizedException;
import feedzupzup.backend.develop.api.DeveloperApi;
import feedzupzup.backend.develop.dto.UpdateAdminPasswordRequest;
import feedzupzup.backend.develop.service.DeveloperService;
import feedzupzup.backend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeveloperController implements DeveloperApi {

    private final DeveloperService developerService;

    @Value("${developer.secret-password}")
    private String secretPassword;

    @Override
    public SuccessResponse<Void> change(
            final UpdateAdminPasswordRequest request
    ) {
        if (!request.developerPassword().equals(secretPassword)) {
            throw new UnauthorizedException("권한이 존재하지 않습니다.");
        }

        developerService.changePassword(request);

        return SuccessResponse.success(HttpStatus.OK);

    }
}
