package lab5.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Изменяемый транспортный объект, используемый при создании события
 */
public class EventDraft implements Serializable {
    private static long serialVersionUID = 1L;

    private String name;
    private LocalDateTime date;
    private String description;

    /**
     * Создает новый черновик события.
     *
     * @param name имя события
     * @param date дата события
     * @param description описание события
     */
    public EventDraft(String name, LocalDateTime date, String description) {
        this.name = name;
        this.date = date;
        this.description = description;
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
}
