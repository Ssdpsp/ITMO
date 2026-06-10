package lab5.application;

/**
 * Реализация команды на основе метаданных и lambda-действия.
 */
public class LambdaCommand implements Command {
    private String name;
    private String description;
    private String syntax;
    private String example;
    private String argumentRules;
    private CommandAction action;

    /**
     * Создает команду.
     *
     * @param name имя
     * @param description описание
     * @param syntax синтаксис
     * @param example пример
     * @param argumentRules правила
     * @param action действие
     */
    public LambdaCommand(
        String name,
        String description,
        String syntax,
        String example,
        String argumentRules,
        CommandAction action
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
    public CommandResult execute(CommandRequest request, CommandContext context) {
        return action.execute(request, context);
    }
}
