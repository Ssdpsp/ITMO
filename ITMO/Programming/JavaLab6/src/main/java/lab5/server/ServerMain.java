package lab5.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import lab5.application.CollectionService;
import lab5.application.CommandHistory;
import lab5.application.CommandResult;
import lab5.domain.model.Ticket;
import lab5.domain.service.IdGenerator;
import lab5.domain.service.SequentialIdGenerator;
import lab5.domain.service.SystemTimeProvider;
import lab5.domain.service.TicketFactory;
import lab5.domain.service.TimeProvider;
import lab5.domain.validation.ValidationService;
import lab5.infrastructure.logging.AppLogger;
import lab5.infrastructure.storage.CsvStorage;
import lab5.infrastructure.storage.LoadReport;
import lab5.infrastructure.storage.ProblemLine;

/**
 * Точка входа сервера.
 */
public class ServerMain {
    private static int DEFAULT_PORT = 5555;
    private static String ENV_FILE = "TICKET_FILE";

    private ServerMain() {
    }

    public static void main(String[] args) {
        int port = args.length > 0 ? parsePort(args[0]) : DEFAULT_PORT;
        Path dataPath = resolveDataPath(args);
        Logger logger = configureJulLogger();
        ServerCommandProcessor processor = null;
        AppLogger appLogger = null;

        logger.info("Запуск сервера. Файл коллекции: " + dataPath);
        try {
            appLogger = new AppLogger(Path.of("logs"));
            ValidationService validationService = new ValidationService();
            CsvStorage storage = new CsvStorage(validationService, appLogger);
            LoadReport loadReport = storage.load(dataPath);
            logger.info(loadReport.getMessage());
            System.out.println(loadReport.getMessage());
            for (ProblemLine line : loadReport.getProblems()) {
                logger.warning("Строка " + line.lineNumber() + " отклонена: " + line.reason());
            }
            if (loadReport.isFatal()) {
                logger.severe("Фатальная ошибка загрузки, сервер не запущен.");
                return;
            }

            LinkedHashSet<Ticket> loadedTickets = loadReport.getTickets();
            IdGenerator idGenerator = new SequentialIdGenerator();
            idGenerator.synchronize(loadedTickets);
            TimeProvider timeProvider = new SystemTimeProvider();
            TicketFactory ticketFactory = new TicketFactory(idGenerator, timeProvider, validationService);
            CollectionService collectionService = new CollectionService(loadedTickets);

            processor = new ServerCommandProcessor(
                collectionService,
                storage,
                dataPath,
                ticketFactory,
                new CommandHistory(15),
                logger
            );

            UdpServer server = new UdpServer(
                new UdpConnectionReceiver(logger),
                new RequestReader(logger),
                processor,
                new ResponseSender(logger),
                new ServerConsoleCommandReader(),
                logger
            );
            server.run(port);
        } catch (IOException exception) {
            logger.log(Level.SEVERE, "Сервер остановлен из-за ошибки", exception);
            System.err.println("Сервер остановлен из-за ошибки: " + exception.getMessage());
        } finally {
            if (processor != null) {
                CommandResult result = processor.saveFromServer();
                System.out.println(result.getMessage());
            }
            if (appLogger != null) {
                try {
                    appLogger.close();
                } catch (IOException exception) {
                    logger.warning("Не удалось закрыть старый логгер: " + exception.getMessage());
                }
            }
            logger.info("Сервер завершил работу.");
            closeHandlers(logger);
        }
    }

    private static Path resolveDataPath(String[] args) {
        if (args.length > 1 && !args[1].isBlank()) {
            return Path.of(args[1]).toAbsolutePath().normalize();
        }
        String fileEnv = System.getenv(ENV_FILE);
        if (fileEnv != null && !fileEnv.isBlank()) {
            return Path.of(fileEnv).toAbsolutePath().normalize();
        }
        return Path.of("data.csv").toAbsolutePath().normalize();
    }

    private static int parsePort(String raw) {
        try {
            int parsed = Integer.parseInt(raw);
            if (parsed > 0 && parsed <= 65535) {
                return parsed;
            }
        } catch (NumberFormatException exception) {
            // default below
        }
        System.err.println("Некорректный порт, используется " + DEFAULT_PORT + ".");
        return DEFAULT_PORT;
    }

    private static Logger configureJulLogger() {
        Logger logger = Logger.getLogger("lab5.server");
        logger.setUseParentHandlers(false);
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }
        logger.setLevel(Level.INFO);
        try {
            Files.createDirectories(Path.of("logs"));
            FileHandler fileHandler = new FileHandler("logs/server.log", true);
            fileHandler.setEncoding("UTF-8");
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException exception) {
            System.err.println("Не удалось открыть файл логов сервера: " + exception.getMessage());
        }

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(consoleHandler);
        return logger;
    }

    private static void closeHandlers(Logger logger) {
        for (Handler handler : logger.getHandlers()) {
            handler.flush();
            handler.close();
            logger.removeHandler(handler);
        }
    }
}
