package lab5.domain.validation.validators;

import java.util.Optional;

/**
 * Обобщенный валидатор поля.
 */
public interface FieldValidator {
    /**
     * Проверяет значение.
     *
     * @param value значение поля
     * @return пустой Optional, если значение корректно, иначе текст ошибки
     */
    Optional<String> validate(Object value);
}
