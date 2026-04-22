package Org;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import Org.application.ApplicationRunner;
import Org.application.CollectionService;
import Org.application.Command;
import Org.application.CommandContext;
import Org.application.CommandFactory;
import Org.application.CommandHistory;
import Org.application.CommandRegistry;
import Org.application.DefaultCommandFactory;
import Org.application.Invoker;
import Org.domain.model.Ticket;
import Org.domain.service.IdGenerator;
import Org.domain.service.SequentialIdGenerator;
import Org.domain.service.SystemTimeProvider;
import Org.domain.service.TicketFactory;
import Org.domain.service.TimeProvider;
import Org.domain.validation.ValidationService;
import Org.infrastructure.input.ConsoleInputSource;
import Org.infrastructure.input.InputManager;
import Org.infrastructure.logging.AppLogger;
import Org.infrastructure.storage.CsvStorage;
import Org.infrastructure.storage.LoadReport;
import Org.infrastructure.storage.ProblemLine;
import Org.presentation.ConsolePresenter;
import Org.presentation.TicketFormReader;

/**
 * Точка входа.
 */
public final class Main {
    private static final String ENV_FILE = "TICKET_FILE";

    private Main() {
    }

    /**
     * Точка входа в приложение.
     *
     * @param args cli args
     */
    public static void main(final String[] args) {
        String fileEnv = System.getenv(ENV_FILE);
        if (fileEnv == null || fileEnv.isBlank()) {
            System.err.println("Не задана переменная окружения " + ENV_FILE + " с путём к CSV-файлу.");
            return;
        }

        Path dataPath = Path.of(fileEnv).toAbsolutePath().normalize();
        try (AppLogger logger = new AppLogger(Path.of("Org/logs"))) {
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