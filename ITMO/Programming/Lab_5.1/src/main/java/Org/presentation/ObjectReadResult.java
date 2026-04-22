package Org.presentation;

import java.util.Optional;

/**
 * Результат чтения объекта.
 *
 * @param <T> payload type
 */
public final class ObjectReadResult<T> {
    private final boolean canceled;
    private final T value;
    private final String error;

    private ObjectReadResult(final boolean canceled, final T value, final String error) {
        this.canceled = canceled;
        this.value = value;
        this.error = error;
    }

    /**
     * Создание успешного результата.
     *
     * @param value value
     * @param <T> type
     * @return result
     */
    public static <T> ObjectReadResult<T> success(final T value) {
        return new ObjectReadResult<>(false, value, null);
    }

    /**
     * Создание отмененного результата.
     *
     * @param <T> type
     * @return result
     */
    public static <T> ObjectReadResult<T> canceled() {
        return new ObjectReadResult<>(true, null, null);
    }

    /**
     * Создание неудачного результата.
     *
     * @param error error text
     * @param <T> type
     * @return result
     */
    public static <T> ObjectReadResult<T> failed(final String error) {
        return new ObjectReadResult<>(false, null, error);
    }

    public boolean isCanceled() {
        return canceled;
    }

    public Optional<T> value() {
        return Optional.ofNullable(value);
    }

    public Optional<String> error() {
        return Optional.ofNullable(error);
    }
}
