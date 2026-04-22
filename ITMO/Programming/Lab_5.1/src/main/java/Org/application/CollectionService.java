package Org.application;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import Org.domain.model.Ticket;

/**
 * инкапсуляция операции сбора данных по всему LinkedHashSet.
 */
public final class CollectionService {
    private final LinkedHashSet<Ticket> tickets;
    private final ZonedDateTime initializationDate;

    /**
     * Создание сервиса с первоначальной коллекцией.
     *
     * @param initial initial tickets
     */
    public CollectionService(final LinkedHashSet<Ticket> initial) {
        this.tickets = new LinkedHashSet<>(initial);
        this.initializationDate = ZonedDateTime.now();
    }

    /**
     * Заменяет коллекцию с новыми данными.
     *
     * @param data new data
     */
    public synchronized void replaceAll(final LinkedHashSet<Ticket> data) {
        tickets.clear();
        tickets.addAll(data);
    }

    /**
     * @return initialization date-time
     */
    public ZonedDateTime getInitializationDate() {
        return initializationDate;
    }

    /**
     * @return collection type name
     */
    public String getCollectionType() {
        return LinkedHashSet.class.getName();
    }

    /**
     * @return size
     */
    public synchronized int size() {
        return tickets.size();
    }

    /**
     * @return snapshot
     */
    public synchronized LinkedHashSet<Ticket> snapshot() {
        return new LinkedHashSet<>(tickets);
    }

    /**
     * Нахождение по id.
     *
     * @param id id
     * @return optional ticket
     */
    public synchronized Optional<Ticket> findById(final long id) {
        return tickets.stream().filter(ticket -> ticket.getId() == id).findFirst();
    }

    /**
     * Добавление билета.
     *
     * @param ticket ticket
     * @return true when added
     */
    public synchronized boolean add(final Ticket ticket) {
        return tickets.add(ticket);
    }

    /**
     * Обновление билета по id.
     *
     * @param id id
     * @param updated updated ticket
     * @return true when updated
     */
    public synchronized boolean updateById(final long id, final Ticket updated) {
        Optional<Ticket> existing = findById(id);
        if (existing.isEmpty()) {
            return false;
        }
        tickets.remove(existing.get());
        tickets.add(updated);
        return true;
    }

    /**
     * Удаление по id.
     *
     * @param id id
     * @return true when removed
     */
    public synchronized boolean removeById(final long id) {
        Optional<Ticket> existing = findById(id);
        if (existing.isEmpty()) {
            return false;
        }
        tickets.remove(existing.get());
        return true;
    }

    /**
     * Очистка коллекции.
     */
    public synchronized void clear() {
        tickets.clear();
    }

    /**
     * @return current max ticket by natural order
     */
    public synchronized Optional<Ticket> max() {
        return tickets.stream().max(Comparator.naturalOrder());
    }

    /**
     * Удалено больше билетов, чем указано.
     *
     * @param reference reference ticket
     * @return removed count
     */
    public synchronized int removeGreater(final Ticket reference) {
        LinkedHashSet<Ticket> toRemove = tickets.stream()
                .filter(ticket -> ticket.compareTo(reference) > 0)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        tickets.removeAll(toRemove);
        return toRemove.size();
    }

    /**
     * Удалено меньше билетов, чем указано.
     *
     * @param reference reference ticket
     * @return removed count
     */
    public synchronized int removeLower(final Ticket reference) {
        LinkedHashSet<Ticket> toRemove = tickets.stream()
                .filter(ticket -> ticket.compareTo(reference) < 0)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        tickets.removeAll(toRemove);
        return toRemove.size();
    }

    /**
     * Счетчик creationDate.
     *
     * @return grouped counts
     */
    public synchronized Map<ZonedDateTime, Long> countByCreationDate() {
        return tickets.stream()
                .collect(Collectors.groupingBy(
                        Ticket::getCreationDate,
                        () -> new TreeMap<>(),
                        Collectors.counting()
                ));
    }

    /**
     * Фильтрация билетов, содержащих подстроку имени.
     *
     * @param substring substring
     * @return filtered set
     */
    public synchronized LinkedHashSet<Ticket> filterContainsName(final String substring) {
        return tickets.stream()
                .filter(ticket -> ticket.getName().contains(substring))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Фильтрация билетов, по которым сумма возврата меньше указанной.
     *
     * @param refundable reference boolean
     * @return filtered set
     */
    public synchronized LinkedHashSet<Ticket> filterLessThanRefundable(final Boolean refundable) {
        return tickets.stream()
                .filter(ticket -> Boolean.compare(ticket.getRefundable(), refundable) < 0)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
