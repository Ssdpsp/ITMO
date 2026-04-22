package Org.application;

/**
 * Командный контракт.
 */
public interface Command {
    /**
     * @return command name
     */
    String name();

    /**
     * @return short command description
     */
    String description();

    /**
     * @return command syntax
     */
    String syntax();

    /**
     * @return command usage example
     */
    String example();

    /**
     * @return input rule notes
     */
    String argumentRules();

    /**
     * Команды ошибок.
     *
     * @param request request
     * @param context context
     * @return result
     */
    CommandResult execute(CommandRequest request, CommandContext context);
}
