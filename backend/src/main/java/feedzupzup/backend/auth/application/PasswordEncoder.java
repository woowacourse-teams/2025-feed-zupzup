package feedzupzup.backend.auth.application;

import feedzupzup.backend.admin.domain.vo.EncodedPassword;
import feedzupzup.backend.admin.domain.vo.Password;

public interface PasswordEncoder {

    EncodedPassword encode(Password password);

    boolean matches(String rawPassword, String encodedPassword);
}
