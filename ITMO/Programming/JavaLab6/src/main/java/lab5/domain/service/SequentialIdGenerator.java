package lab5.domain.service;

import java.util.Collection;
import java.util.Objects;
import lab5.domain.model.Event;
import lab5.domain.model.Ticket;

/**
 * генератор id
 */
public class SequentialIdGenerator implements IdGenerator {
    private long ticketIdCounter;
    private int eventIdCounter;

    /**
     * Создает генератор с начальными счетчиками.
     */
    public SequentialIdGenerator() {
        this.ticketIdCounter = 0L;
        this.eventIdCounter = 0;
    }

    @Override
    public long nextTicketId() {
        ticketIdCounter += 1L;
        return ticketIdCounter;
    }

    @Override
    public int nextEventId() {
        eventIdCounter += 1;
        return eventIdCounter;
    }

    @Override
    public void synchronize(Collection<Ticket> tickets) {
        this.ticketIdCounter = tickets.stream()
            .mapToLong(Ticket::getId)
            .max()
            .orElse(0L);
        this.eventIdCounter = tickets.stream()
            .map(Ticket::getEvent)
            .filter(Objects::nonNull)
            .mapToInt(Event::getId)
            .max()
            .orElse(0);
    }
}
