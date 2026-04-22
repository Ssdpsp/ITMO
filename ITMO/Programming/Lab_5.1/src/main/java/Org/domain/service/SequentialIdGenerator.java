package Org.domain.service;

import java.util.Collection;
import Org.domain.model.Event;
import Org.domain.model.Ticket;

/**
 * Генератор последовательных идентификаторов, синхронизированных с загруженными данными.
 */
public final class SequentialIdGenerator implements IdGenerator {
    private long ticketIdCounter;
    private int eventIdCounter;

    /**
     * Создание генератора с начальными счётчиками.
     */
    public SequentialIdGenerator() {
        this.ticketIdCounter = 0L;
        this.eventIdCounter = 0;
    }

    @Override
    public synchronized long nextTicketId() {
        ticketIdCounter += 1L;
        return ticketIdCounter;
    }

    @Override
    public synchronized int nextEventId() {
        eventIdCounter += 1;
        return eventIdCounter;
    }

    @Override
    public synchronized void synchronize(final Collection<Ticket> tickets) {
        long maxTicketId = 0L;
        int maxEventId = 0;
        for (Ticket ticket : tickets) {
            if (ticket.getId() > maxTicketId) {
                maxTicketId = ticket.getId();
            }
            Event event = ticket.getEvent();
            if (event != null && event.getId() > maxEventId) {
                maxEventId = event.getId();
            }
        }
        this.ticketIdCounter = maxTicketId;
        this.eventIdCounter = maxEventId;
    }
}
