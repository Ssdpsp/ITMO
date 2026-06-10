package lab5.application;

import java.io.IOException;
import lab5.infrastructure.input.InputManager;
import lab5.infrastructure.input.ReadLine;
import lab5.infrastructure.logging.AppLogger;
import lab5.presentation.ConsolePresenter;

/**
 * Основной цикл управления приложением.
 */
public class ApplicationRunner {
    private InputManager inputManager;
    private Invoker invoker;
    private CommandContext context;
    private ConsolePresenter presenter;
    private AppLogger logger;

    /**
     * Создает объект запуска.
     *
     * @param inputManager менеджер ввода
     * @param invoker объект вызова команд
     * @param context контекст команды
     * @param presenter объект вывода
     * @param logger логгер
     */
    public ApplicationRunner(
        InputManager inputManager,
        Invoker invoker,
        CommandContext context,
        ConsolePresenter presenter,
        AppLogger logger
    ) {
        this.inputManager = inputManager;
        this.invoker = invoker;
        this.context = context;
        this.presenter = presenter;
        this.logger = logger;
    }

    /**
     * Запускает интерактивный цикл обработки.
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
