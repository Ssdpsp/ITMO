package lab5.domain.validation.validators;

import java.util.Optional;

/**
 * Проверяет нижнюю границу для long.
 */
public class LongGreaterThanValidator implements FieldValidator {
    private long border;

    /**
     * Создает валидатор.
     *
     * @param border нижняя граница, не включительно
     */
    public LongGreaterThanValidator(long border) {
        this.border = border;
    }

    @Override
    public Optional<String> validate(Object value) {
        Long number = (Long) value;
        if (number == null) {
            return Optional.of("Поле не может быть null.");
        }
        if (number <= border) {
            return Optional.of("Значение должно быть больше " + border + ".");
        }
        return Optional.empty();
    }
}
