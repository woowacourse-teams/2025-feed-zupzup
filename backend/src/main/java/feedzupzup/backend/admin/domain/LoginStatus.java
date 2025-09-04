package feedzupzup.backend.admin.domain;

public enum LoginStatus {
    LOGIN,
    LOGOUT;

    public boolean isLoggedIn() {
        return this == LOGIN;
    }
}
