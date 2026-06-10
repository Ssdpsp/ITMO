package lab5.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Событие, связанное с билето
 */
public class Event implements Serializable {
    private static long serialVersionUID = 1L;
    private int id;
    private String name;
    private LocalDateTime date;
    private String description;

    /**
     * Создает событие.
     *
     * @param id id события
     * @param name имя события
     * @param date дата события
     * @param description описание события
     */
    public Event(int id, String name, LocalDateTime date, String description) {
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event event)) {
            return false;
        } return id == event.id
            && Objects.equals(name, event.name)
            && Objects.equals(date, event.date)
            && Objects.equals(description, event.description);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, name, date, description);
    }
}
