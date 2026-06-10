package lab5.application;

/**
 * Функциональный делегат выполнения команды.
 */
@FunctionalInterface
public interface CommandAction {
    /**
     * Выполняет логику команды.
     *
     * @param request запрос команды
     * @param context контекст
     * @return результат
     */
    CommandResult execute(CommandRequest request, CommandContext context);
}
