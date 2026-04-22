package Org.domain.service;

import java.util.Collection;
import Org.domain.model.Ticket;

/**
 * Генерация уникальных идентификаторов для сущностей.
 */
public interface IdGenerator {
    /**
     * @return next unique ticket id
     */
    long nextTicketId();

    /**
     * @return next unique event id
     */
    int nextEventId();

    /**
     * Синхронизированный генератор с загруженной коллекцией.
     *
     * @param tickets existing tickets
     */
    void synchronize(Collection<Ticket> tickets);
}
