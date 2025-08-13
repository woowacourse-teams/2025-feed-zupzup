package feedzupzup.backend.auth.application;

import feedzupzup.backend.admin.domain.vo.Password;

public interface PasswordEncoder {

    Password encode(Password password);

    boolean matches(String rawPassword, String encodedPassword);
}
