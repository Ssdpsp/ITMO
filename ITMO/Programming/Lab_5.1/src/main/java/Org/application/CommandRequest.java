package Org.application;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import Org.infrastructure.input.ReadLine;

/**
 * Обработанный вызов команды.
 */
public final class CommandRequest {
    private final String rawLine;
    private final String commandName;
    private final String argumentLine;
    private final ReadLine sourceLine;

    /**
     * Создание запроса.
     *
     * @param rawLine raw line
     * @param commandName command name
     * @param argumentLine argument tail
     * @param sourceLine source metadata
     */
    public CommandRequest(
            final String rawLine,
            final String commandName,
            final String argumentLine,
            final ReadLine sourceLine
    ) {
        this.rawLine = rawLine;
        this.commandName = commandName;
        this.argumentLine = argumentLine == null ? "" : argumentLine.trim();
        this.sourceLine = sourceLine;
    }

    public String getRawLine() {
        return rawLine;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getArgumentLine() {
        return argumentLine;
    }

    public ReadLine getSourceLine() {
        return sourceLine;
    }

    /**
     * Разбивание строки аргумента по пробелу.
     *
     * @return tokens list
     */
    public List<String> argumentTokens() {
        if (argumentLine.isBlank()) {
            return List.of();
        }
        return Arrays.stream(argumentLine.split("\\s+")).collect(Collectors.toList());
    }
}
