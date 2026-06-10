package lab5.domain.validation;

import java.util.List;

/**
 * Выбрасывается при ошибке валидации.
 */
public class ValidationException extends RuntimeException {
    private List<String> errors;

    /**
     * Создает исключение.
     *
     * @param errors ошибки валидации
     */
    public ValidationException(List<String> errors) {
        super(String.join("; ", errors));
        this.errors = errors;
    }

    /**
     * @return ошибки валидации
     */
    public List<String> getErrors() {
        return errors;
    }
}
