package Org.application;

/**
 * Реализация команд на основе метаданных.
 */
public final class LambdaCommand implements Command {
    private final String name;
    private final String description;
    private final String syntax;
    private final String example;
    private final String argumentRules;
    private final CommandAction action;

    /**
     * Создание команд.
     *
     * @param name name
     * @param description description
     * @param syntax syntax
     * @param example example
     * @param argumentRules rules
     * @param action action
     */
    public LambdaCommand(
            final String name,
            final String description,
            final String syntax,
            final String example,
            final String argumentRules,
            final CommandAction action
    ) {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
        this.example = example;
        this.argumentRules = argumentRules;
        this.action = action;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String syntax() {
        return syntax;
    }

    @Override
    public String example() {
        return example;
    }

    @Override
    public String argumentRules() {
        return argumentRules;
    }

    @Override
    public CommandResult execute(final CommandRequest request, final CommandContext context) {
        return action.execute(request, context);
    }
}