package lab5.application;

import java.util.Locale;
import java.util.Optional;
import lab5.infrastructure.input.InputMode;
import lab5.infrastructure.input.ReadLine;

/**
 * Разбирает строку команды, находит команду и запускает выполнение.
 */
public class Invoker {
    private CommandRegistry registry;

    /**
     * Создает объект вызова команд.
     *
     * @param registry реестр команд
     */
    public Invoker(CommandRegistry registry) {
        this.registry = registry;
    }

    /**
     * Выполняет строку команды.
     *
     * @param sourceLine исходная строка
     * @param context контекст
     * @return результат
     */
    public CommandResult invoke(ReadLine sourceLine, CommandContext context) {
        String raw = sourceLine.text();
        String trimmed = raw == null ? "" : stripUtf8Bom(raw).trim();
        if (trimmed.isEmpty()) {
            return CommandResult.success("");
        }

        int splitIndex = indexOfWhitespace(trimmed);
        String name = splitIndex < 0 ? trimmed : trimmed.substring(0, splitIndex);
        String args = splitIndex < 0 ? "" : trimmed.substring(splitIndex + 1).trim();

        String normalizedName = name.toLowerCase(Locale.ROOT);
        Optional<Command> command = registry.find(normalizedName);
        if (command.isEmpty()) {
            return CommandResult.error("Неизвестная команда: " + name + ". Используйте help.");
        }

        context.setCurrentLine(sourceLine);
        context.history().add(normalizedName);
        context.logger().logTech("Команда: " + trimmed + " | source=" + sourceLine.sourceName());

        CommandRequest request = new CommandRequest(trimmed, normalizedName, args, sourceLine);
        try {
            CommandResult result = command.get().execute(request, context);
            if (result.getStatus() == CommandResultStatus.ERROR && sourceLine.mode() == InputMode.SCRIPT) {
                context.logger().logTech(
                    "Ошибка в скрипте: line=" + sourceLine.lineNumber()
                        + ", command='" + sourceLine.text() + "', reason='" + result.getMessage() + "'"
                );
            }
            return result;
        } catch (Exception exception) {
            context.logger().logError("Необработанная ошибка команды: " + trimmed, exception);
            if (sourceLine.mode() == InputMode.SCRIPT) {
                context.logger().logTech(
                    "Ошибка в скрипте: line=" + sourceLine.lineNumber()
                        + ", command='" + sourceLine.text() + "', reason='" + exception.getMessage() + "'"
                );
            }
            return CommandResult.error("Ошибка выполнения команды: " + exception.getMessage());
        }
    }

    private int indexOfWhitespace(String line) {
        for (int i = 0; i < line.length(); i += 1) {
            if (Character.isWhitespace(line.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private String stripUtf8Bom(String value) {
        if (value != null && !value.isEmpty() && value.charAt(0) == '\uFEFF') {
            return value.substring(1);
        }
        return value;
    }
}
