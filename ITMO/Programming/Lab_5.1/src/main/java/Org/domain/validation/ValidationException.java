package Org.domain.validation;

import java.util.List;

/**
 * Выброс при сбое проверки.
 */
public final class ValidationException extends RuntimeException {
    private final List<String> errors;

    /**
     * Создание исключения.
     *
     * @param errors validation errors
     */
    public ValidationException(final List<String> errors) {
        super(String.join("; ", errors));
        this.errors = errors;
    }

    /**
     * @return validation errors
     */
    public List<String> getErrors() {
        return errors;
    }
}
