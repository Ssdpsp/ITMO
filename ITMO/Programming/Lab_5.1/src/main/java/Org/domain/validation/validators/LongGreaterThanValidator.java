package Org.domain.validation.validators;

import java.util.Optional;

/**
 * Проверка нижней границы для long.
 */
public final class LongGreaterThanValidator implements FieldValidator<Long> {
    private final long border;

    /**
     * Создание валидатора.
     *
     * @param border lower border (exclusive)
     */
    public LongGreaterThanValidator(final long border) {
        this.border = border;
    }

    @Override
    public Optional<String> validate(final Long value) {
        if (value == null) {
            return Optional.of("Поле не может быть null.");
        }
        if (value <= border) {
            return Optional.of("Значение должно быть больше " + border + ".");
        }
        return Optional.empty();
    }
}
