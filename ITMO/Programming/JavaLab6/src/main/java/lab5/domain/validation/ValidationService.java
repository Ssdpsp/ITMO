package lab5.domain.validation;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lab5.domain.model.Coordinates;
import lab5.domain.model.Event;
import lab5.domain.model.Ticket;
import lab5.domain.validation.validators.DoubleGreaterThanValidator;
import lab5.domain.validation.validators.FieldValidator;
import lab5.domain.validation.validators.IntGreaterThanValidator;
import lab5.domain.validation.validators.IntMaxValidator;
import lab5.domain.validation.validators.LongGreaterThanValidator;
import lab5.domain.validation.validators.NotNullValidator;
import lab5.domain.validation.validators.StringNotBlankValidator;

/**
 * Выполняет валидацию на уровне полей и объектов.
 */
public class ValidationService {
    private FieldValidator stringNotBlankValidator;
    private FieldValidator intPositiveValidator;
    private FieldValidator coordinatesXMaxValidator;
    private FieldValidator coordinatesYMinValidator;
    private FieldValidator longPositiveValidator;
    private FieldValidator notNullValidator;

    /**
     * Создает сервис валидации со стандартными валидаторами.
     */
    public ValidationService() {
        this.stringNotBlankValidator = new StringNotBlankValidator();
        this.intPositiveValidator = new IntGreaterThanValidator(0);
        this.coordinatesXMaxValidator = new IntMaxValidator(222);
        this.coordinatesYMinValidator = new DoubleGreaterThanValidator(-234.0);
        this.longPositiveValidator = new LongGreaterThanValidator(0L);
        this.notNullValidator = new NotNullValidator();
    }

    /**
     * Проверяет поле Ticket.name.
     *
     * @param value значение
     * @return результат
     */
    public ValidationResult validateTicketName(String value) {
        return fromOptional(stringNotBlankValidator.validate(value), "name");
    }

    /**
     * Проверяет поле Coordinates.x.
     *
     * @param value значение
     * @return результат
     */
    public ValidationResult validateCoordinatesX(Integer value) {
        ValidationResult result = fromOptional(notNullValidator.validate(value), "coordinates.x");
        if (value != null) {
            result = result.merge(fromOptional(coordinatesXMaxValidator.validate(value), "coordinates.x"));
        }
        return result;
    }

    /**
     * Проверяет поле Coordinates.y.
     *
     * @param value значение
     * @return результат
     */
    public ValidationResult validateCoordinatesY(Double value) {
        ValidationResult result = fromOptional(notNullValidator.validate(value), "coordinates.y");
        if (value != null) {
            result = result.merge(fromOptional(coordinatesYMinValidator.validate(value), "coordinates.y"));
        }
        return result;
    }

    /**
     * Проверяет поле Ticket.price.
     *
     * @param value значение
     * @return результат
     */
    public ValidationResult validatePrice(Integer value) {
        return fromOptional(intPositiveValidator.validate(value), "price");
    }

    /**
     * Проверяет поле Ticket.refundable.
     *
     * @param value значение
     * @return результат
     */
    public ValidationResult validateRefundable(Boolean value) {
        return fromOptional(notNullValidator.validate(value), "refundable");
    }

    /**
     * Проверяет поле Event.name.
     *
     * @param value значение
     * @return результат
     */
    public ValidationResult validateEventName(String value) {
        return fromOptional(stringNotBlankValidator.validate(value), "event.name");
    }

    /**
     * Проверяет поле Event.date.
     *
     * @param value значение
     * @return результат
     */
    public ValidationResult validateEventDate(LocalDateTime value) {
        return fromOptional(notNullValidator.validate(value), "event.date");
    }

    /**
     * Проверяет поле Event.description.
     *
     * @param value значение
     * @return результат
     */
    public ValidationResult validateEventDescription(String value) {
        return fromOptional(stringNotBlankValidator.validate(value), "event.description");
    }

    /**
     * Проверяет полный объект билета.
     *
     * @param ticket билет
     * @return результат валидации
     */
    public ValidationResult validateTicket(Ticket ticket) {
        ValidationResult result = ValidationResult.ok();
        if (ticket == null) {
            return result.plus("ticket: объект не может быть null.");
        }

        result = result.merge(fromOptional(longPositiveValidator.validate(ticket.getId()), "id"));
        result = result.merge(validateTicketName(ticket.getName()));
        result = result.merge(fromOptional(notNullValidator.validate(ticket.getCoordinates()), "coordinates"));
        result = result.merge(fromOptional(notNullValidator.validate(ticket.getCreationDate()), "creationDate"));
        result = result.merge(validatePrice(ticket.getPrice()));
        result = result.merge(validateRefundable(ticket.getRefundable()));

        Coordinates coordinates = ticket.getCoordinates();
        if (coordinates != null) {
            result = result.merge(validateCoordinatesX(coordinates.getX()));
            result = result.merge(validateCoordinatesY(coordinates.getY()));
        }

        Event event = ticket.getEvent();
        if (event != null) {
            result = result.merge(validateEvent(event));
        }
        return result;
    }

    /**
     * Проверяет объект события.
     *
     * @param event событие
     * @return результат
     */
    public ValidationResult validateEvent(Event event) {
        ValidationResult result = ValidationResult.ok();
        if (event == null) {
            return result.plus("event: объект не может быть null.");
        }
        result = result.merge(fromOptional(intPositiveValidator.validate(event.getId()), "event.id"));
        result = result.merge(validateEventName(event.getName()));
        result = result.merge(validateEventDate(event.getDate()));
        result = result.merge(validateEventDescription(event.getDescription()));
        return result;
    }

    /**
     * Выбрасывает исключение, если объект невалиден.
     *
     * @param ticket билет для проверки
     */
    public void validateOrThrow(Ticket ticket) {
        ValidationResult result = validateTicket(ticket);
        if (!result.isValid()) {
            throw new ValidationException(result.getErrors());
        }
    }
    public ValidationResult validateCreationDate(ZonedDateTime value) {
        return fromOptional(notNullValidator.validate(value), "creationDate");
    }

    private ValidationResult fromOptional(Optional<String> error, String field) {
        if (error.isEmpty()) {
            return ValidationResult.ok();
        }
        List<String> list = new ArrayList<>();
        list.add(field + ": " + error.get());
        return new ValidationResult(list);
    }
}
