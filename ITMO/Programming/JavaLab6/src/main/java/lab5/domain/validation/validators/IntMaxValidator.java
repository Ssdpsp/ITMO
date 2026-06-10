package lab5.domain.validation.validators;

import java.util.Optional;

/**
 * Проверяет верхнюю границу для целого числа.
 */
public class IntMaxValidator implements FieldValidator {
    private int max;

    /**
     * Создает валидатор.
     *
     * @param max максимально допустимое значение
     */
    public IntMaxValidator(int max) {
        this.max = max;
    }

    @Override
    public Optional<String> validate(Object value) {
        Integer number = (Integer) value;
        if (number == null) {
            return Optional.of("Поле не может быть null.");
        }
        if (number > max) {
            return Optional.of("Максимальное значение поля: " + max + ".");
        }
        return Optional.empty();
    }
}
