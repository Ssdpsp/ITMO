package Org.application;

import java.io.IOException;
import Org.infrastructure.input.InputManager;
import Org.infrastructure.input.ReadLine;
import Org.infrastructure.logging.AppLogger;
import Org.presentation.ConsolePresenter;

/**
 * Основной цикл приложения.
 */
public final class ApplicationRunner {
    private final InputManager inputManager;
    private final Invoker invoker;
    private final CommandContext context;
    private final ConsolePresenter presenter;
    private final AppLogger logger;

    /**
     * Создание runner.
     *
     * @param inputManager input manager
     * @param invoker command invoker
     * @param context command context
     * @param presenter presenter
     * @param logger logger
     */
    public ApplicationRunner(
            final InputManager inputManager,
            final Invoker invoker,
            final CommandContext context,
            final ConsolePresenter presenter,
            final AppLogger logger
    ) {
        this.inputManager = inputManager;
        this.invoker = invoker;
        this.context = context;
        this.presenter = presenter;
        this.logger = logger;
    }

    /**
     * Запуск цикла обработки.
     */
    public void run() {
        logger.logTech("Приложение запущено.");
        while (true) {
            presenter.printCommandPrompt(inputManager.currentMode());
            ReadLine sourceLine;
            try {
                sourceLine = inputManager.readLine();
            } catch (IOException exception) {
                presenter.printError("Ошибка чтения ввода: " + exception.getMessage());
                logger.logError("Ошибка чтения входных данных", exception);
                break;
            }
            if (sourceLine == null) {
                logger.logTech("Ввод завершён (EOF).");
                break;
            }

            CommandResult result = invoker.invoke(sourceLine, context);
            presenter.present(result);
            if (result.getStatus() == CommandResultStatus.EXIT) {
                break;
            }
        }
        logger.logTech("Приложение завершено.");
    }
}