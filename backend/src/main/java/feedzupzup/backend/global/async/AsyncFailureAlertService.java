package feedzupzup.backend.global.async;

public interface AsyncFailureAlertService {

    void alert(final Long asyncTaskFailureId);
}
