package Org.domain.model;

import java.time.LocalDateTime;

/**
 * Изменяемый объект, используемый для создания события на основе пользовательского ввода.
 */
public final class EventDraft {
    private final String name;
    private final LocalDateTime date;
    private final String description;

    /**
     * Создание черновика события.
     *
     * @param name event name
     * @param date event date
     * @param description event description
     */
    public EventDraft(final String name, final LocalDateTime date, final String description) {
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
