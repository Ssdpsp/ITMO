package lab5.application;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lab5.domain.model.Ticket;

/**
 * Инкапсулирует операции с коллекцией LinkedHashSet.
 */
public class CollectionService {
    private LinkedHashSet<Ticket> tickets;
    private ZonedDateTime initializationDate;

    /**
     * Создает сервис с начальной коллекцией.
     *
     * @param initial начальные билеты
     */
    public CollectionService(LinkedHashSet<Ticket> initial) {
        this.tickets = new LinkedHashSet<>(initial);
        this.initializationDate = ZonedDateTime.now();
    }

    /**
     * Заменяет коллекцию новыми данными.
     *
     * @param data новые данные
     */
    public void replaceAll(LinkedHashSet<Ticket> data) {
        tickets.clear();
        tickets.addAll(data);
    }

    /**
     * @return дата и время инициализации
     */
    public ZonedDateTime getInitializationDate() {
        return initializationDate;
    }

    /**
     * @return имя типа коллекции
     */
    public String getCollectionType() {
        return LinkedHashSet.class.getName();
    }

    /**
     * @return размер
     */
    public int size() {
        return tickets.size();
    }

    /**
     * @return снимок коллекции
     */
    public LinkedHashSet<Ticket> snapshot() {
        return new LinkedHashSet<>(tickets);
    }

    /**
     * @return снимок коллекции, отсортированный по имени билета
     */
    public LinkedHashSet<Ticket> snapshotSortedByName() {
        return sortByName(tickets);
    }

    /**
     * Ищет по id.
     *
     * @param id id
     * @return optional с билетом
     */
    public Optional<Ticket> findById(long id) {
        return tickets.stream().filter(ticket -> ticket.getId() == id).findFirst();
    }

    /**
     * Добавляет билет.
     *
     * @param ticket билет
     * @return true, если добавлено
     */
    public boolean add(Ticket ticket) {
        return tickets.add(ticket);
    }

    /**
     * Обновляет билет по id.
     *
     * @param id id
     * @param updated обновленный билет
     * @return true, если обновлено
     */
    public boolean updateById(long id, Ticket updated) {
        Optional<Ticket> existing = findById(id);
        if (existing.isEmpty()) {
            return false;
        }
        tickets.remove(existing.get());
        tickets.add(updated);
        return true;
    }

    /**
     * Удаляет по id.
     *
     * @param id id
     * @return true, если удалено
     */
    public boolean removeById(long id) {
        Optional<Ticket> existing = findById(id);
        if (existing.isEmpty()) {
            return false;
        }
        tickets.remove(existing.get());
        return true;
    }

    /**
     * Очищает коллекцию.
     */
    public void clear() {
        tickets.clear();
    }

    /**
     * @return текущий максимальный билет по естественному порядку
     */
    public Optional<Ticket> max() {
        return tickets.stream().max(Comparator.naturalOrder());
    }

    /**
     * Удаляет билеты, которые больше образца.
     *
     * @param reference билет-образец
     * @return количество удаленных элементов
     */
    public int removeGreater(Ticket reference) {
        LinkedHashSet<Ticket> toRemove = tickets.stream()
            .filter(ticket -> ticket.compareTo(reference) > 0)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        tickets.removeAll(toRemove);
        return toRemove.size();
    }

    /**
     * Удаляет билеты, которые меньше образца.
     *
     * @param reference билет-образец
     * @return количество удаленных элементов
     */
    public int removeLower(Ticket reference) {
        LinkedHashSet<Ticket> toRemove = tickets.stream()
            .filter(ticket -> ticket.compareTo(reference) < 0)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        tickets.removeAll(toRemove);
        return toRemove.size();
    }

    /**
     * Считает количество по creationDate.
     *
     * @return сгруппированные количества
     */
    public Map<ZonedDateTime, Long> countByCreationDate() {
        return tickets.stream()
            .collect(Collectors.groupingBy(
                Ticket::getCreationDate,
                () -> new TreeMap<>(),
                Collectors.counting()
            ));
    }

    /**
     * Фильтрует билеты, name которых содержит подстроку.
     *
     * @param substring подстрока
     * @return отфильтрованный набор
     */
    public LinkedHashSet<Ticket> filterContainsName(String substring) {
        return tickets.stream()
            .filter(ticket -> ticket.getName().contains(substring))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Фильтрует билеты, у которых refundable меньше заданного значения.
     *
     * @param refundable логический образец
     * @return отфильтрованный набор
     */
    public LinkedHashSet<Ticket> filterLessThanRefundable(Boolean refundable) {
        return tickets.stream()
            .filter(ticket -> Boolean.compare(ticket.getRefundable(), refundable) < 0)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Сортирует любой набор билетов по name для сетевых ответов.
     *
     * @param data билеты
     * @return отсортированный набор
     */
    public LinkedHashSet<Ticket> sortByName(LinkedHashSet<Ticket> data) {
        return data.stream()
            .sorted(Comparator.comparing(Ticket::getName, String.CASE_INSENSITIVE_ORDER)
                .thenComparingLong(Ticket::getId))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
