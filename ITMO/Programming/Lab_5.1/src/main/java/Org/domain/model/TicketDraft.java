package Org.domain.model;

/**
 * Изменяемый объект, используемый при создании билета на основе ввода пользователя.
 */
public final class TicketDraft {
    private final String name;
    private final Coordinates coordinates;
    private final int price;
    private final Boolean refundable;
    private final TicketType type;
    private final EventDraft event;

    /**
     * Создание чернового билета.
     *
     * @param name name
     * @param coordinates coordinates
     * @param price price
     * @param refundable refundable flag
     * @param type type
     * @param event event draft
     */
    public TicketDraft(
            final String name,
            final Coordinates coordinates,
            final int price,
            final Boolean refundable,
            final TicketType type,
            final EventDraft event
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