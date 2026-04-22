package Org.application;

/**
 * Передача выполнения команды.
 */
@FunctionalInterface
public interface CommandAction {
    /**
     * Выполнение логики команды.
     *
     * @param request command request
     * @param context context
     * @return result
     */
    CommandResult execute(CommandRequest request, CommandContext context);
}

