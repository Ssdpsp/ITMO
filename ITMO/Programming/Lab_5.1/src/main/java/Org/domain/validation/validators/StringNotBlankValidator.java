package Org.domain.validation.validators;

import java.util.Optional;

/**
 * Проверка, что строка не является нулевой и непустой.
 */
public final class StringNotBlankValidator implements FieldValidator<String> {
    @Override
    public Optional<String> validate(final String value) {
        if (value == null) {
            return Optional.of("Поле не может быть null.");
        }
        if (value.trim().isEmpty()) {
            return Optional.of("Строка не может быть пустой.");
        }
        return Optional.empty();
    }
}

