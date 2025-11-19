package feedzupzup.backend.global.domain;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Exception;
}
