package lab5.domain.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Представляет результат валидации.
 */
public class ValidationResult {
    private List<String> errors;

    /**
     * Создает результат с ошибками.
     *
     * @param errors список ошибок
     */
    public ValidationResult(List<String> errors) {
        this.errors = new ArrayList<>(errors);
    }

    /**
     * @return успешный результат
     */
    public static ValidationResult ok() {
        return new ValidationResult(Collections.emptyList());
    }

    /**
     * @return true, если ошибок нет
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * @return неизменяемый список ошибок
     */
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Возвращает новый результат со всеми текущими ошибками и еще одной.
     *
     * @param error текст ошибки
     * @return обновленный результат
     */
    public ValidationResult plus(String error) {
        List<String> merged = new ArrayList<>(errors);
        merged.add(error);
        return new ValidationResult(merged);
    }

    /**
     * Возвращает объединенный результат.
     *
     * @param other второй результат
     * @return объединенный результат
     */
    public ValidationResult merge(ValidationResult other) {
        List<String> merged = new ArrayList<>(errors);
        merged.addAll(other.errors);
        return new ValidationResult(merged);
    }
}
