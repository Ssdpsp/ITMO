package Org.domain.service;

import java.time.ZonedDateTime;
import Org.domain.model.Event;
import Org.domain.model.EventDraft;
import Org.domain.model.Ticket;
import Org.domain.model.TicketDraft;
import Org.domain.validation.ValidationService;

/**
 * Создание билета и автоматическкая генерация полей.
 */
public final class TicketFactory {
    private final IdGenerator idGenerator;
    private final TimeProvider timeProvider;
    private final ValidationService validationService;

    /**
     * Создание factory.
     *
     * @param idGenerator id generator
     * @param timeProvider time provider
     * @param validationService validation service
     */
    public TicketFactory(
            final IdGenerator idGenerator,
            final TimeProvider timeProvider,
            final ValidationService validationService
    ) {
        this.idGenerator = idGenerator;
        this.timeProvider = timeProvider;
        this.validationService = validationService;
    }

    /**
     * Создание билетов с генерацией id и creationDate.
     *
     * @param draft ticket draft
     * @return valid ticket
     */
    public Ticket createNew(final TicketDraft draft) {
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
     * Создание обновленного билета, сохраняя неизмененные поля старого билета.
     *
     * @param existing existing ticket
     * @param draft new draft
     * @return updated ticket
     */
    public Ticket createForUpdate(final Ticket existing, final TicketDraft draft) {
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
     * Создание временного билета, используемого в качестве ссылки для сравнения в командах, которые не изменяются.
     *
     * @param draft draft
     * @return transient valid ticket
     */
    public Ticket createTransient(final TicketDraft draft) {
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

    private Event createNewEvent(final EventDraft eventDraft) {
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
