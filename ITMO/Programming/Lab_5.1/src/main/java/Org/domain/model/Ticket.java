package Org.domain.model;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Objects;

/**
 * Основной объект, хранится в коллекции.
 */
public final class Ticket implements Comparable<Ticket> {
    private final long id;
    private final String name;
    private final Coordinates coordinates;
    private final ZonedDateTime creationDate;
    private final int price;
    private final Boolean refundable;
    private final TicketType type;
    private final Event event;

    /**
     * Создание билета.
     *
     * @param id id
     * @param name name
     * @param coordinates coordinates
     * @param creationDate creation date
     * @param price price
     * @param refundable refundable flag
     * @param type ticket type
     * @param event event
     */
    public Ticket(
            final long id,
            final String name,
            final Coordinates coordinates,
            final ZonedDateTime creationDate,
            final int price,
            final Boolean refundable,
            final TicketType type,
            final Event event
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
    public int compareTo(final Ticket other) {
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
    public boolean equals(final Object o) {
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