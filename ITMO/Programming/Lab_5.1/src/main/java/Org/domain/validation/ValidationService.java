package Org.domain.validation;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import Org.domain.model.Coordinates;
import Org.domain.model.Event;
import Org.domain.model.Ticket;
import Org.domain.validation.validators.DoubleGreaterThanValidator;
import Org.domain.validation.validators.FieldValidator;
import Org.domain.validation.validators.IntGreaterThanValidator;
import Org.domain.validation.validators.IntMaxValidator;
import Org.domain.validation.validators.LongGreaterThanValidator;
import Org.domain.validation.validators.NotNullValidator;
import Org.domain.validation.validators.StringNotBlankValidator;

/**
 * Выполнение проверки на уровне полей и объектов.
 */
public final class ValidationService {
    private final FieldValidator<String> stringNotBlankValidator;
    private final FieldValidator<Integer> intPositiveValidator;
    private final FieldValidator<Integer> coordinatesXMaxValidator;
    private final FieldValidator<Double> coordinatesYMinValidator;
    private final FieldValidator<Long> longPositiveValidator;
    private final FieldValidator<Object> notNullValidator;

    /**
     * Создание проверки с помощью валидаторов.
     */
    public ValidationService() {
        this.stringNotBlankValidator = new StringNotBlankValidator();
        this.intPositiveValidator = new IntGreaterThanValidator(0);
        this.coordinatesXMaxValidator = new IntMaxValidator(222);
        this.coordinatesYMinValidator = new DoubleGreaterThanValidator(-234.0);
        this.longPositiveValidator = new LongGreaterThanValidator(0L);
        this.notNullValidator = new NotNullValidator<>();
    }

    /**
     * Проверка поля имени билета.
     *
     * @param value value
     * @return result
     */
    public ValidationResult validateTicketName(final String value) {
        return fromOptional(stringNotBlankValidator.validate(value), "name");
    }

    /**
     * Проверка поля координаты.x.
     *
     * @param value value
     * @return result
     */
    public ValidationResult validateCoordinatesX(final Integer value) {
        ValidationResult result = fromOptional(notNullValidator.validate(value), "coordinates.x");
        if (value != null) {
            result = result.merge(fromOptional(coordinatesXMaxValidator.validate(value), "coordinates.x"));
        }
        return result;
    }

    /**
     * Проверка поля координаты.y.
     *
     * @param value value
     * @return result
     */
    public ValidationResult validateCoordinatesY(final Double value) {
        ValidationResult result = fromOptional(notNullValidator.validate(value), "coordinates.y");
        if (value != null) {
            result = result.merge(fromOptional(coordinatesYMinValidator.validate(value), "coordinates.y"));
        }
        return result;
    }

    /**
     * Проверка поля цена билета.
     *
     * @param value value
     * @return result
     */
    public ValidationResult validatePrice(final Integer value) {
        return fromOptional(intPositiveValidator.validate(value), "price");
    }

    /**
     * Проеврка поля возвратный.
     *
     * @param value value
     * @return result
     */
    public ValidationResult validateRefundable(final Boolean value) {
        return fromOptional(notNullValidator.validate(value), "refundable");
    }

    /**
     * Проверка поля названия мероприятия.
     *
     * @param value value
     * @return result
     */
    public ValidationResult validateEventName(final String value) {
        return fromOptional(stringNotBlankValidator.validate(value), "event.name");
    }

    /**
     * Проверка поля даты мероприятия.
     *
     * @param value value
     * @return result
     */
    public ValidationResult validateEventDate(final LocalDateTime value) {
        return fromOptional(notNullValidator.validate(value), "event.date");
    }

    /**
     * Проверка поля описание билета.
     *
     * @param value value
     * @return result
     */
    public ValidationResult validateEventDescription(final String value) {
        return fromOptional(stringNotBlankValidator.validate(value), "event.description");
    }

    /**
     * Полная проверка билета.
     *
     * @param ticket ticket
     * @return validation result
     */
    public ValidationResult validateTicket(final Ticket ticket) {
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
     * Проверка объекта событие.
     *
     * @param event event
     * @return result
     */
    public ValidationResult validateEvent(final Event event) {
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
     * Выброс, если он недействителен.
     *
     * @param ticket ticket to validate
     */
    public void validateOrThrow(final Ticket ticket) {
        ValidationResult result = validateTicket(ticket);
        if (!result.isValid()) {
            throw new ValidationException(result.getErrors());
        }
    }

    /**
     * Проеврка поля дата создания.
     *
     * @param value value
     * @return result
     */
    public ValidationResult validateCreationDate(final ZonedDateTime value) {
        return fromOptional(notNullValidator.validate(value), "creationDate");
    }

    private ValidationResult fromOptional(final Optional<String> error, final String field) {
        if (error.isEmpty()) {
            return ValidationResult.ok();
        }
        List<String> list = new ArrayList<>();
        list.add(field + ": " + error.get());
        return new ValidationResult(list);
    }
}
