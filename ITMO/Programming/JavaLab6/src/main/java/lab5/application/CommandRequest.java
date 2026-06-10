package lab5.application;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lab5.infrastructure.input.ReadLine;

/**
 * Разобранный запрос на вызов команды.
 */
public class CommandRequest {
    private String rawLine;
    private String commandName;
    private String argumentLine;
    private ReadLine sourceLine;

    /**
     * Создает запрос.
     *
     * @param rawLine исходная строка
     * @param commandName имя команды
     * @param argumentLine хвост аргументов
     * @param sourceLine метаданные источника
     */
    public CommandRequest(
        String rawLine,
        String commandName,
        String argumentLine,
        ReadLine sourceLine
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
     * Разбивает строку аргументов по пробельным символам.
     *
     * @return список токенов
     */
    public List<String> argumentTokens() {
        if (argumentLine.isBlank()) {
            return List.of();
        }
        return Arrays.stream(argumentLine.split("\\s+")).collect(Collectors.toList());
    }
}
