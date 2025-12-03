package feedzupzup.backend.auth.fake;

import feedzupzup.backend.admin.domain.vo.EncodedPassword;
import feedzupzup.backend.admin.domain.vo.Password;
import feedzupzup.backend.auth.application.PasswordEncoder;

public class FakePasswordEncoder implements PasswordEncoder {

    private static final String PREFIX = "encoded_";

    @Override
    public EncodedPassword encode(Password password) {
        return new EncodedPassword(PREFIX + password.value());
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encodedPassword.equals(PREFIX + rawPassword);
    }
}
