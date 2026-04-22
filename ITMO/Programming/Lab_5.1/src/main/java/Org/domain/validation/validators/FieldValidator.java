package Org.domain.validation.validators;

import java.util.Optional;

/**
 * Универсальный валидатор полей.
 *
 * @param <T> field type
 */
public interface FieldValidator<T> {
    /**
     * Проверка значения.
     *
     * @param value field value
     * @return empty when valid, otherwise error text
     */
    Optional<String> validate(T value);
}