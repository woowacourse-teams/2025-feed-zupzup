package feedzupzup.backend.global.util;

public class NumberValidator {

    public static void validateNonNegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("음수의 값은 허용하지 않습니다.");
        }
    }
}
