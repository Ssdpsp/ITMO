package lab5.application;

/**
 * Контракт команды.
 */
public interface Command {
    /**
     * @return имя команды
     */
    String name();

    /**
     * @return краткое описание команды
     */
    String description();

    /**
     * @return синтаксис команды
     */
    String syntax();

    /**
     * @return пример использования команды
     */
    String example();

    /**
     * @return заметки о правилах ввода
     */
    String argumentRules();

    /**
     * Выполняет команду.
     *
     * @param request запрос
     * @param context контекст
     * @return результат
     */
    CommandResult execute(CommandRequest request, CommandContext context);
}
