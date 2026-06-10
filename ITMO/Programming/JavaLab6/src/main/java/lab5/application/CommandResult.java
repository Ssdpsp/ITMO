package lab5.application;

import java.io.Serializable;

/**
 * Структурированный результат команды, подходящий для вывода в UI или сетевой передачи.
 */
public class CommandResult implements Serializable {
    private static long serialVersionUID = 1L;

    private CommandResultStatus status;
    private String message;
    private Object payload;

    /**
     * Создает результат команды.
     *
     * @param status статус
     * @param message пользовательское сообщение
     * @param payload необязательная полезная нагрузка
     */
    public CommandResult(CommandResultStatus status, String message, Object payload) {
        this.status = status;
        this.message = message;
        this.payload = payload;
    }

    public static CommandResult success(String message) {
        return new CommandResult(CommandResultStatus.SUCCESS, message, null);
    }

    public static CommandResult success(String message, Object payload) {
        return new CommandResult(CommandResultStatus.SUCCESS, message, payload);
    }

    public static CommandResult error(String message) {
        return new CommandResult(CommandResultStatus.ERROR, message, null);
    }

    public static CommandResult exit(String message) {
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
