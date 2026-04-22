package Org.domain.validation.validators;

import java.util.Optional;

/**
 * Проверяет, что целочисленное значение больше границы.
 */
public final class IntGreaterThanValidator implements FieldValidator<Integer> {
    private final int border;

    /**
     * Создание валидатора.
     *
     * @param border lower border (exclusive)
     */
    public IntGreaterThanValidator(final int border) {
        this.border = border;
    }

    @Override
    public Optional<String> validate(final Integer value) {
        if (value == null) {
            return Optional.of("Поле не может быть null.");
        }
        if (value <= border) {
            return Optional.of("Значение должно быть больше " + border + ".");
        }
        return Optional.empty();
    }
}
