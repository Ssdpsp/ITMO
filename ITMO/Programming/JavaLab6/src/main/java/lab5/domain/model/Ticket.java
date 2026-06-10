package lab5.domain.model;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Objects;

/**
 * Основной доменный объект, хранящийся в управляемой коллекции
 */
public class Ticket implements Comparable<Ticket>, Serializable {
    private static long serialVersionUID = 1L;
    private long id;
    private String name;
    private Coordinates coordinates;
    private ZonedDateTime creationDate;
    private int price;
    private Boolean refundable;
    private TicketType type;
    private Event event;

    /**
     * Создает билет.
     *
     * @param id id
     * @param name имя
     * @param coordinates координаты
     * @param creationDate дата создания
     * @param price цена
     * @param refundable флаг возвратности
     * @param type тип билета
     * @param event событие
     */
    public Ticket(
        long id,
        String name,
        Coordinates coordinates,
        ZonedDateTime creationDate,
        int price,
        Boolean refundable,
        TicketType type,
        Event event
    ) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.price = price;
        this.refundable = refundable;
        this.type = type;
        this.event = event;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public int getPrice() {
        return price;
    }

    public Boolean getRefundable() {
        return refundable;
    }

    public TicketType getType() {
        return type;
    }

    public Event getEvent() {
        return event;
    }

    @Override
    public int compareTo(Ticket other) {
        return Comparator.comparingInt(Ticket::getPrice)
            .thenComparing(Ticket::getName, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(Ticket::getRefundable)
            .thenComparing(Ticket::getType, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(ticket -> ticket.getCoordinates().getX())
            .thenComparing(ticket -> ticket.getCoordinates().getY())
            .thenComparing(Ticket::getCreationDate)
            .compare(this, other);
    }

    @Override
    public String toString() {
        return "Ticket{id="
            + id
            + ", name='"
            + name
            + "', coordinates="
            + coordinates
            + ", creationDate="
            + creationDate
            + ", price="
            + price
            + ", refundable="
            + refundable
            + ", type="
            + type
            + ", event="
            + event
            + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ticket ticket)) {
            return false;
        }
        return id == ticket.id;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
