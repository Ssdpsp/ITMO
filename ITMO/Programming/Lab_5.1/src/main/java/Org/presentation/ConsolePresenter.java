package Org.presentation;

import java.io.PrintStream;
import Org.application.CommandResult;
import Org.application.CommandResultStatus;
import Org.infrastructure.input.InputMode;

/**
 * Слои представления для консоли.
 */
public final class ConsolePresenter {
    private final PrintStream out;
    private final PrintStream err;

    /**
     * Создание presenter.
     *
     * @param out stdout stream
     * @param err stderr stream
     */
    public ConsolePresenter(final PrintStream out, final PrintStream err) {
        this.out = out;
        this.err = err;
    }

    /**
     * Создание результата команд.
     *
     * @param result result
     */
    public void present(final CommandResult result) {
        if (result == null || result.getMessage() == null || result.getMessage().isBlank()) {
            return;
        }
        if (result.getStatus() == CommandResultStatus.ERROR) {
            err.println(result.getMessage());
            return;
        }
        out.println(result.getMessage());
    }

    /**
     * Вывод запроса.
     *
     * @param mode input mode
     */
    public void printCommandPrompt(final InputMode mode) {
        if (mode == InputMode.INTERACTIVE) {
            out.print("> ");
        }
    }

    /**
     * Вывод запроса на заполнение поля.
     *
     * @param mode mode
     * @param fieldMessage prompt
     */
    public void printFieldPrompt(final InputMode mode, final String fieldMessage) {
        if (mode == InputMode.INTERACTIVE) {
            out.println(fieldMessage);
            out.print(">> ");
        }
    }

    /**
     * Печать пользовательского сообщения.
     *
     * @param message message
     */
    public void println(final String message) {
        out.println(message);
    }

    /**
     * Вывод сообщения об ошибке пользователя.
     *
     * @param message message
     */
    public void printError(final String message) {
        err.println(message);
    }
}
