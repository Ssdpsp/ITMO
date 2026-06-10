package lab5.domain.validation.validators;

import java.util.Optional;

/**
 * Проверяет, что значение не равно null.
 */
public class NotNullValidator implements FieldValidator {
    @Override
    public Optional<String> validate(Object value) {
        if (value == null) {
            return Optional.of("значение не должно быть null.");
        }
        return Optional.empty();
    }
}
