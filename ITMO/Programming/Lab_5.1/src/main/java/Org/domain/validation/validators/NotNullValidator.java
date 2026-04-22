package Org.domain.validation.validators;

import java.util.Optional;

/**
 * Проверка, что значение не равно null.
 *
 * @param <T> type
 */
public final class NotNullValidator<T> implements FieldValidator<T> {
    @Override
    public Optional<String> validate(final T value) {
        if (value == null) {
            return Optional.of("Поле не может быть null.");
        }
        return Optional.empty();
    }
}
