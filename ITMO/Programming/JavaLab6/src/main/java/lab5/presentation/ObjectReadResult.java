package lab5.presentation;

import java.util.Optional;

/**
 * Результат интерактивного чтения объекта.
 */
public class ObjectReadResult {
    private boolean canceled;
    private Object value;
    private String error;

    private ObjectReadResult(boolean canceled, Object value, String error) {
        this.canceled = canceled;
        this.value = value;
        this.error = error;
    }

    /**
     * Создает успешный результат.
     *
     * @param value значение
     * @return результат
     */
    public static ObjectReadResult success(Object value) {
        return new ObjectReadResult(false, value, null);
    }

    /**
     * Создает результат отмены.
     *
     * @return результат
     */
    public static ObjectReadResult canceled() {
        return new ObjectReadResult(true, null, null);
    }

    /**
     * Создает результат ошибки.
     *
     * @param error текст ошибки
     * @return результат
     */
    public static ObjectReadResult failed(String error) {
        return new ObjectReadResult(false, null, error);
    }

    public boolean isCanceled() {
        return canceled;
    }

    public Optional<Object> value() {
        return Optional.ofNullable(value);
    }

    public Optional<String> error() {
        return Optional.ofNullable(error);
    }
}
