package Org.domain.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Представление результата проверки.
 */
public final class ValidationResult {
    private final List<String> errors;

    /**
     * Создание результата с ошибками.
     *
     * @param errors list of errors
     */
    public ValidationResult(final List<String> errors) {
        this.errors = new ArrayList<>(errors);
    }

    /**
     * @return successful result
     */
    public static ValidationResult ok() {
        return new ValidationResult(Collections.emptyList());
    }

    /**
     * @return true when there are no errors
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * @return immutable errors
     */
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Возвращение нового результата со всеми текущими ошибками плюс один.
     *
     * @param error error text
     * @return updated result
     */
    public ValidationResult plus(final String error) {
        List<String> merged = new ArrayList<>(errors);
        merged.add(error);
        return new ValidationResult(merged);
    }

    /**
     * Возвращение объединенного результата.
     *
     * @param other second result
     * @return merged result
     */
    public ValidationResult merge(final ValidationResult other) {
        List<String> merged = new ArrayList<>(errors);
        merged.addAll(other.errors);
        return new ValidationResult(merged);
    }
}
