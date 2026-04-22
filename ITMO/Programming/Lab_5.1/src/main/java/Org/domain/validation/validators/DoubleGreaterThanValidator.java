package Org.domain.validation.validators;

import java.util.Optional;

/**
 * Проверка нижней границы для doubles.
 */
public final class DoubleGreaterThanValidator implements FieldValidator<Double> {
    private final double border;

    /**
     * Создание валидатора.
     *
     * @param border lower border (exclusive)
     */
    public DoubleGreaterThanValidator(final double border) {
        this.border = border;
    }

    @Override
    public Optional<String> validate(final Double value) {
        if (value == null) {
            return Optional.of("Поле не может быть null.");
        }
        if (value <= border) {
            return Optional.of("Значение должно быть больше " + border + ".");
        }
        return Optional.empty();
    }
}
