package lab5.domain.validation.validators;

import java.util.Optional;

/**
 * Проверяет, что целое значение больше границы.
 */
public class IntGreaterThanValidator implements FieldValidator {
    private int border;

    /**
     * Создает валидатор.
     *
     * @param border нижняя граница, не включительно
     */
    public IntGreaterThanValidator(int border) {
        this.border = border;
    }

    @Override
    public Optional<String> validate(Object value) {
        Integer number = (Integer) value;
        if (number == null) {
            return Optional.of("Поле не может быть null.");
        }
        if (number <= border) {
            return Optional.of("Значение должно быть больше " + border + ".");
        }
        return Optional.empty();
    }
}
