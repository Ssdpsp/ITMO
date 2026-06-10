package lab5.client;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lab5.domain.model.TicketDraft;
import lab5.infrastructure.input.InputManager;
import lab5.infrastructure.input.ReadLine;
import lab5.presentation.ConsolePresenter;
import lab5.presentation.ObjectReadResult;
import lab5.presentation.TicketFormReader;
import lab5.shared.CommandArgument;
import lab5.shared.CommandCode;
import lab5.shared.CommandPacket;

/**
 * Разбирает консольную строку и создает сериализуемый объект команды.
 */
public class ClientCommandBuilder {
    private InputManager inputManager;
    private TicketFormReader ticketFormReader;
    private ConsolePresenter presenter;

    public ClientCommandBuilder(
        InputManager inputManager,
        TicketFormReader ticketFormReader,
        ConsolePresenter presenter
    ) {
        this.inputManager = inputManager;
        this.ticketFormReader = ticketFormReader;
        this.presenter = presenter;
    }

    public BuildResult build(ReadLine sourceLine, long requestId) {
        String trimmed = normalize(sourceLine.text());
        if (trimmed.isBlank()) {
            return BuildResult.localContinue();
        }

        int split = indexOfWhitespace(trimmed);
        String commandName = split < 0 ? trimmed : trimmed.substring(0, split);
        String argumentLine = split < 0 ? "" : trimmed.substring(split + 1).trim();
        Optional<CommandCode> code = CommandCode.fromName(commandName);
        if (code.isEmpty()) {
            presenter.printError("Неизвестная команда: " + commandName + ". Используйте help.");
            return BuildResult.localContinue();
        }

        return switch (code.get()) {
            case EXIT -> buildExit(argumentLine);
            case SAVE -> {
                presenter.printError("Команда save доступна только на сервере, клиент ее не отправляет.");
                yield BuildResult.localContinue();
            }
            case EXECUTE_SCRIPT -> buildExecuteScript(argumentLine);
            case ADD, ADD_IF_MAX, REMOVE_GREATER, REMOVE_LOWER -> buildElementCommand(requestId, code.get(), argumentLine);
            case UPDATE -> buildUpdate(requestId, argumentLine);
            case REMOVE_BY_ID -> buildRemoveById(requestId, argumentLine);
            case FILTER_CONTAINS_NAME -> buildFilterContainsName(requestId, argumentLine);
            case FILTER_LESS_THAN_REFUNDABLE -> buildFilterLessThanRefundable(requestId, argumentLine);
            case HELP, INFO, SHOW, CLEAR, GROUP_COUNTING_BY_CREATION_DATE, HISTORY -> buildNoArgs(requestId, code.get(), argumentLine);
        };
    }

    private BuildResult buildExit(String argumentLine) {
        if (!argumentLine.isBlank()) {
            presenter.printError("Команда exit не принимает аргументы.");
            return BuildResult.localContinue();
        }
        presenter.println("Клиент завершает работу.");
        return BuildResult.exit();
    }

    private BuildResult buildExecuteScript(String argumentLine) {
        if (argumentLine.isBlank()) {
            presenter.printError("Синтаксис: execute_script file_name");
            return BuildResult.localContinue();
        }
        try {
            var resolved = inputManager.resolveScriptPath(argumentLine);
            inputManager.pushScript(resolved);
            presenter.println("Скрипт добавлен в стек: " + resolved.toAbsolutePath());
        } catch (IllegalStateException exception) {
            presenter.printError(exception.getMessage());
        } catch (IOException exception) {
            presenter.printError("Не удалось открыть скрипт: " + exception.getMessage());
        }
        return BuildResult.localContinue();
    }

    private BuildResult buildElementCommand(long requestId, CommandCode code, String argumentLine) {
        if (!argumentLine.isBlank()) {
            presenter.printError("Команда " + code.commandName() + " не принимает аргументы в строке.");
            return BuildResult.localContinue();
        }
        TicketDraft draft = readDraft();
        if (draft == null) {
            return BuildResult.localContinue();
        }
        return BuildResult.send(new CommandPacket(requestId, code, List.of(), draft));
    }

    private BuildResult buildUpdate(long requestId, String argumentLine) {
        Long id = parsePositiveLong(argumentLine);
        if (id == null) {
            presenter.printError("Синтаксис: update id");
            return BuildResult.localContinue();
        }
        TicketDraft draft = readDraft();
        if (draft == null) {
            return BuildResult.localContinue();
        }
        return BuildResult.send(new CommandPacket(
            requestId,
            CommandCode.UPDATE,
            List.of(CommandArgument.of("id", id)),
            draft
        ));
    }

    private BuildResult buildRemoveById(long requestId, String argumentLine) {
        Long id = parsePositiveLong(argumentLine);
        if (id == null) {
            presenter.printError("Синтаксис: remove_by_id id");
            return BuildResult.localContinue();
        }
        return BuildResult.send(new CommandPacket(
            requestId,
            CommandCode.REMOVE_BY_ID,
            List.of(CommandArgument.of("id", id)),
            null
        ));
    }

    private BuildResult buildFilterContainsName(long requestId, String argumentLine) {
        if (argumentLine.isBlank()) {
            presenter.printError("Синтаксис: filter_contains_name name");
            return BuildResult.localContinue();
        }
        return BuildResult.send(new CommandPacket(
            requestId,
            CommandCode.FILTER_CONTAINS_NAME,
            List.of(CommandArgument.of("substring", argumentLine)),
            null
        ));
    }

    private BuildResult buildFilterLessThanRefundable(long requestId, String argumentLine) {
        var value = ticketFormReader.parseBoolean(argumentLine);
        if (argumentLine.isBlank() || value.isEmpty()) {
            presenter.printError("Синтаксис: filter_less_than_refundable true/false");
            return BuildResult.localContinue();
        }
        return BuildResult.send(new CommandPacket(
            requestId,
            CommandCode.FILTER_LESS_THAN_REFUNDABLE,
            List.of(CommandArgument.of("refundable", value.get())),
            null
        ));
    }

    private BuildResult buildNoArgs(long requestId, CommandCode code, String argumentLine) {
        if (!argumentLine.isBlank()) {
            presenter.printError("Команда " + code.commandName() + " не принимает аргументы.");
            return BuildResult.localContinue();
        }
        return BuildResult.send(CommandPacket.noArgs(requestId, code));
    }

    private TicketDraft readDraft() {
        ObjectReadResult result = ticketFormReader.readTicketDraft();
        if (result.isCanceled()) {
            presenter.printError("Ввод объекта отменен.");
            return null;
        }
        if (result.error().isPresent()) {
            presenter.printError(result.error().get());
            return null;
        }
        Object value = result.value().orElse(null);
        if (value instanceof TicketDraft draft) {
            return draft;
        }
        return null;
    }

    private Long parsePositiveLong(String raw) {
        if (raw == null || raw.isBlank() || raw.split("\\s+").length != 1) {
            return null;
        }
        try {
            long value = Long.parseLong(raw.trim());
            return value > 0 ? value : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String normalize(String raw) {
        String line = raw == null ? "" : raw;
        if (!line.isEmpty() && line.charAt(0) == '\uFEFF') {
            line = line.substring(1);
        }
        return line.trim();
    }

    private int indexOfWhitespace(String line) {
        for (int i = 0; i < line.length(); i += 1) {
            if (Character.isWhitespace(line.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public static class BuildResult {
        private boolean send;
        private boolean exit;
        private CommandPacket packet;

        private BuildResult(boolean send, boolean exit, CommandPacket packet) {
            this.send = send;
            this.exit = exit;
            this.packet = packet;
        }

        public static BuildResult send(CommandPacket packet) {
            return new BuildResult(true, false, packet);
        }

        public static BuildResult exit() {
            return new BuildResult(false, true, null);
        }

        public static BuildResult localContinue() {
            return new BuildResult(false, false, null);
        }

        public boolean shouldSend() {
            return send;
        }

        public boolean shouldExit() {
            return exit;
        }

        public CommandPacket packet() {
            return packet;
        }
    }
}
