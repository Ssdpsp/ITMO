package lab5.domain.service;

import java.util.Collection;
import lab5.domain.model.Ticket;

/**
 * Генерирует уникальные идентификаторы для доменных сущностей.
 */
public interface IdGenerator {
    /**
     * @return следующий уникальный id билета
     */
    long nextTicketId();

    /**
     * @return следующий уникальный id события
     */
    int nextEventId();

    /**
     * Синхронизирует генератор с уже загруженной коллекцией.
     *
     * @param tickets существующие билеты
     */
    void synchronize(Collection<Ticket> tickets);
}
