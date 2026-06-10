package lab5.presentation;

import java.io.PrintStream;
import lab5.application.CommandResult;
import lab5.application.CommandResultStatus;
import lab5.infrastructure.input.InputMode;

/**
 * Слой представления для вывода в консоль.
 */
public class ConsolePresenter {
    private PrintStream out;
    private PrintStream err;

    /**
     * Создает объект вывода.
     *
     * @param out поток stdout
     * @param err поток stderr
     */
    public ConsolePresenter(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    /**
     * Выводит результат команды.
     *
     * @param result результат
     */
    public void present(CommandResult result) {
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
     * Печатает приглашение, когда режим интерактивный.
     *
     * @param mode режим ввода
     */
    public void printCommandPrompt(InputMode mode) {
        if (mode == InputMode.INTERACTIVE) {
            out.print("> ");
        }
    }

    /**
     * Печатает приглашение для поля в интерактивном режиме.
     *
     * @param mode режим
     * @param fieldMessage приглашение
     */
    public void printFieldPrompt(InputMode mode, String fieldMessage) {
        if (mode == InputMode.INTERACTIVE) {
            out.println(fieldMessage);
            out.print(">> ");
        }
    }

    /**
     * Печатает обычное пользовательское сообщение.
     *
     * @param message сообщение
     */
    public void println(String message) {
        out.println(message);
    }

    /**
     * Печатает сообщение об ошибке для пользователя.
     *
     * @param message сообщение
     */
    public void printError(String message) {
        err.println(message);
    }
}
