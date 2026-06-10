package lab5;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import lab5.application.ApplicationRunner;
import lab5.application.CollectionService;
import lab5.application.Command;
import lab5.application.CommandContext;
import lab5.application.CommandFactory;
import lab5.application.CommandHistory;
import lab5.application.CommandRegistry;
import lab5.application.DefaultCommandFactory;
import lab5.application.Invoker;
import lab5.domain.model.Ticket;
import lab5.domain.service.IdGenerator;
import lab5.domain.service.SequentialIdGenerator;
import lab5.domain.service.SystemTimeProvider;
import lab5.domain.service.TicketFactory;
import lab5.domain.service.TimeProvider;
import lab5.domain.validation.ValidationService;
import lab5.infrastructure.input.ConsoleInputSource;
import lab5.infrastructure.input.InputManager;
import lab5.infrastructure.logging.AppLogger;
import lab5.infrastructure.storage.CsvStorage;
import lab5.infrastructure.storage.LoadReport;
import lab5.infrastructure.storage.ProblemLine;
import lab5.presentation.ConsolePresenter;
import lab5.presentation.TicketFormReader;

/**
 * Точка входа.
 */
public class Main {
    private static String ENV_FILE = "TICKET_FILE";

    private Main() {
    }

    /**
     * Точка входа приложения.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        String fileEnv = System.getenv(ENV_FILE);
        if (fileEnv == null || fileEnv.isBlank()) {
            System.err.println("Не задана переменная окружения " + ENV_FILE + " с путём к CSV-файлу.");
            return;
        } Path dataPath = Path.of(fileEnv).toAbsolutePath().normalize();
        try (AppLogger logger = new AppLogger(Path.of("logs"))) {
            ValidationService validationService = new ValidationService();
            CsvStorage storage = new CsvStorage(validationService, logger);

            LoadReport loadReport = storage.load(dataPath);
            System.out.println(loadReport.getMessage());
            for (ProblemLine line : loadReport.getProblems()) {
                System.err.println(
                    "Строка " + line.lineNumber() + " отклонена: " + line.reason()
                        + " | raw: " + line.rawLine()
                );
            }
            if (loadReport.isFatal()) {
                logger.logUser("Фатальная ошибка загрузки. Приложение не запущено.");
                return;
            }

            LinkedHashSet<Ticket> loadedTickets = loadReport.getTickets();
            IdGenerator idGenerator = new SequentialIdGenerator();
            idGenerator.synchronize(loadedTickets);
            TimeProvider timeProvider = new SystemTimeProvider();
            TicketFactory ticketFactory = new TicketFactory(idGenerator, timeProvider, validationService);

            CollectionService collectionService = new CollectionService(loadedTickets);
            ConsolePresenter presenter = new ConsolePresenter(System.out, System.err);

            try (InputManager inputManager = new InputManager(new ConsoleInputSource(System.in), logger)) {
                TicketFormReader ticketFormReader = new TicketFormReader(inputManager, presenter, validationService);
                CommandHistory history = new CommandHistory(15);
                CommandContext context = new CommandContext(
                    collectionService,
                    storage,
                    dataPath,
                    inputManager,
                    ticketFormReader,
                    ticketFactory,
                    history,
                    logger
                );

                CommandFactory commandFactory = new DefaultCommandFactory();
                CommandRegistry registry = new CommandRegistry();
                for (Command command : commandFactory.createCommands()) {
                    registry.register(command);
                }
                context.setRegistry(registry);

                Invoker invoker = new Invoker(registry);
                ApplicationRunner runner = new ApplicationRunner(inputManager, invoker, context, presenter, logger);
                runner.run();
            } catch (IOException exception) {
                logger.logError("Ошибка закрытия ресурсов ввода", exception);
                System.err.println("Ошибка ввода/вывода: " + exception.getMessage());
            }
        } catch (IOException exception) {
            System.err.println("Не удалось инициализировать логирование: " + exception.getMessage());
        }
    }
}
