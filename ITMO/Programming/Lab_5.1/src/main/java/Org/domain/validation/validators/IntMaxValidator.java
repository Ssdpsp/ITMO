package Org.domain.validation.validators;

import java.util.Optional;

/**
 * Проверка максимальной границы для целого числа.
 */
public final class IntMaxValidator implements FieldValidator<Integer> {
    private final int max;

    /**
     * Создание валидатора.
     *
     * @param max max allowed value
     */
    public IntMaxValidator(final int max) {
        this.max = max;
    }

    @Override
    public Optional<String> validate(final Integer value) {
        if (value == null) {
            return Optional.of("Поле не может быть null.");
        }
        if (value > max) {
            return Optional.of("Максимальное значение поля: " + max + ".");
        }
        return Optional.empty();
    }
}
