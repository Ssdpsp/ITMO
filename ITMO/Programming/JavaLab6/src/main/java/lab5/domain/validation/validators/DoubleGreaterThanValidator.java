package lab5.domain.validation.validators;

import java.util.Optional;

/**
 * Проверяет нижнюю границу для double.
 */
public class DoubleGreaterThanValidator implements FieldValidator {
    private double border;

    /**
     * Создает валидатор.
     *
     * @param border нижняя граница, не включительно
     */
    public DoubleGreaterThanValidator(double border) {
        this.border = border;
    }

    @Override
    public Optional<String> validate(Object value) {
        Double number = (Double) value;
        if (number == null) {
            return Optional.of("Поле не может быть null.");
        }
        if (number <= border) {
            return Optional.of("Значение должно быть больше " + border + ".");
        }
        return Optional.empty();
    }
}
