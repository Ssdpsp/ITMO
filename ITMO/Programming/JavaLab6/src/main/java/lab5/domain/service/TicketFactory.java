package lab5.domain.service;

import java.time.ZonedDateTime;
import lab5.domain.model.Event;
import lab5.domain.model.EventDraft;
import lab5.domain.model.Ticket;
import lab5.domain.model.TicketDraft;
import lab5.domain.validation.ValidationService;

/**
 * Создает билеты и применяет автоматическую генерацию полей.
 */
public class TicketFactory {
    private IdGenerator idGenerator;
    private TimeProvider timeProvider;
    private ValidationService validationService;

    /**
     * Создает фабрику.
     *
     * @param idGenerator генератор id
     * @param timeProvider поставщик времени
     * @param validationService сервис валидации
     */
    public TicketFactory(
        IdGenerator idGenerator,
        TimeProvider timeProvider,
        ValidationService validationService
    ) {
        this.idGenerator = idGenerator;
        this.timeProvider = timeProvider;
        this.validationService = validationService;
    }

    /**
     * Создает билет со сгенерированными id и creationDate.
     *
     * @param draft черновик билета
     * @return валидный билет
     */
    public Ticket createNew(TicketDraft draft) {
        Ticket ticket = new Ticket(
            idGenerator.nextTicketId(),
            draft.getName(),
            draft.getCoordinates(),
            timeProvider.now(),
            draft.getPrice(),
            draft.getRefundable(),
            draft.getType(),
            createNewEvent(draft.getEvent())
        );
        validationService.validateOrThrow(ticket);
        return ticket;
    }

    /**
     * Создает обновленный билет, сохраняя неизменяемые сгенерированные поля старого билета.
     *
     * @param existing существующий билет
     * @param draft новый черновик
     * @return обновленный билет
     */
    public Ticket createForUpdate(Ticket existing, TicketDraft draft) {
        Event event = null;
        if (draft.getEvent() != null) {
            int eventId = existing.getEvent() != null ? existing.getEvent().getId() : idGenerator.nextEventId();
            event = new Event(
                eventId,
                draft.getEvent().getName(),
                draft.getEvent().getDate(),
                draft.getEvent().getDescription()
            );
        }
        Ticket ticket = new Ticket(
            existing.getId(),
            draft.getName(),
            draft.getCoordinates(),
            existing.getCreationDate(),
            draft.getPrice(),
            draft.getRefundable(),
            draft.getType(),
            event
        );
        validationService.validateOrThrow(ticket);
        return ticket;
    }

    /**
     * Создает временный билет, который используется как образец для сравнения в командах без вставки.
     *
     * @param draft черновик
     * @return временный валидный билет
     */
    public Ticket createTransient(TicketDraft draft) {
        Event event = null;
        if (draft.getEvent() != null) {
            EventDraft eventDraft = draft.getEvent();
            event = new Event(1, eventDraft.getName(), eventDraft.getDate(), eventDraft.getDescription());
        }
        Ticket ticket = new Ticket(
            1L,
            draft.getName(),
            draft.getCoordinates(),
            ZonedDateTime.parse("2000-01-01T00:00:00Z"),
            draft.getPrice(),
            draft.getRefundable(),
            draft.getType(),
            event
        );
        validationService.validateOrThrow(ticket);
        return ticket;
    }

    private Event createNewEvent(EventDraft eventDraft) {
        if (eventDraft == null) {
            return null;
        }
        return new Event(
            idGenerator.nextEventId(),
            eventDraft.getName(),
            eventDraft.getDate(),
            eventDraft.getDescription()
        );
    }
}
