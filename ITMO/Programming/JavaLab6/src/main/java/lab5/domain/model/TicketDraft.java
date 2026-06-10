package lab5.domain.model;

import java.io.Serializable;

/**
 * Изменяемый транспортный объект, используемый при создании билета
 */
public class TicketDraft implements Serializable {
    private static long serialVersionUID = 1L;

    private String name;
    private Coordinates coordinates;
    private int price;
    private Boolean refundable;
    private TicketType type;
    private EventDraft event;

    /**
     * Создает черновик билета.
     *
     * @param name имя
     * @param coordinates координаты
     * @param price цена
     * @param refundable флаг возвратности
     * @param type тип
     * @param event черновик события
     */
    public TicketDraft(
        String name,
        Coordinates coordinates,
        int price,
        Boolean refundable,
        TicketType type,
        EventDraft event
    ) {
        this.name = name;
        this.coordinates = coordinates;
        this.price = price;
        this.refundable = refundable;
        this.type = type;
        this.event = event;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
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

    public EventDraft getEvent() {
        return event;
    }
}
