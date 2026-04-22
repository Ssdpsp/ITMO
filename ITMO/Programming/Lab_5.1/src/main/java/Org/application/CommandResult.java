package Org.application;

/**
 * Структурированный результат команды.
 */
public final class CommandResult {
    private final CommandResultStatus status;
    private final String message;
    private final Object payload;

    /**
     * Создание результата команды.
     *
     * @param status status
     * @param message user message
     * @param payload optional payload
     */
    public CommandResult(final CommandResultStatus status, final String message, final Object payload) {
        this.status = status;
        this.message = message;
        this.payload = payload;
    }

    public static CommandResult success(final String message) {
        return new CommandResult(CommandResultStatus.SUCCESS, message, null);
    }

    public static CommandResult success(final String message, final Object payload) {
        return new CommandResult(CommandResultStatus.SUCCESS, message, payload);
    }

    public static CommandResult error(final String message) {
        return new CommandResult(CommandResultStatus.ERROR, message, null);
    }

    public static CommandResult exit(final String message) {
        return new CommandResult(CommandResultStatus.EXIT, message, null);
    }

    public CommandResultStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getPayload() {
        return payload;
    }
}
