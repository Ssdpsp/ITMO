package lab5.domain.validation.validators;

import java.util.Optional;

/**
 * Проверяет, что строка не равна null и не пустая.
 */
public class StringNotBlankValidator implements FieldValidator {
    @Override
    public Optional<String> validate(Object value) {
        String text = (String) value;
        if (text == null) {
            return Optional.of("Поле не может быть null.");
        }
        if (text.trim().isEmpty()) {
            return Optional.of("Строка не может быть пустой.");
        }
        return Optional.empty();
    }
}
