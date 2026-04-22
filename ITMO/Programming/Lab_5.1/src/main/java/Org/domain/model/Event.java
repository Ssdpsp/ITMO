package Org.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Мероприятие, привязанное к билету.
 */
public final class Event {
    private final int id;
    private final String name;
    private final LocalDateTime date;
    private final String description;

    /**
     * Создание мероприятия.
     *
     * @param id event id
     * @param name event name
     * @param date event date
     * @param description event description
     */
    public Event(final int id, final String name, final LocalDateTime date, final String description) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Event{id="
                + id
                + ", name='"
                + name
                + "', date="
                + date
                + ", description='"
                + description
                + "'}";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event event)) {
            return false;
        }
        return id == event.id
                && Objects.equals(name, event.name)
                && Objects.equals(date, event.date)
                && Objects.equals(description, event.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, date, description);
    }
}