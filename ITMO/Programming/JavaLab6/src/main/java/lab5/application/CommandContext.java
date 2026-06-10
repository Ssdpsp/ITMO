package lab5.application;

import java.nio.file.Path;
import lab5.domain.service.TicketFactory;
import lab5.infrastructure.input.InputManager;
import lab5.infrastructure.input.ReadLine;
import lab5.infrastructure.logging.AppLogger;
import lab5.infrastructure.storage.CsvStorage;
import lab5.presentation.TicketFormReader;

/**
 * Общий контекст выполнения, передаваемый командам.
 */
public class CommandContext {
    private CollectionService collectionService;
    private CsvStorage csvStorage;
    private Path dataFile;
    private InputManager inputManager;
    private TicketFormReader ticketFormReader;
    private TicketFactory ticketFactory;
    private CommandHistory history;
    private AppLogger logger;
    private CommandRegistry registry;
    private ReadLine currentLine;

    /**
     * Создает контекст.
     *
     * @param collectionService сервис коллекции
     * @param csvStorage хранилище
     * @param dataFile файл данных
     * @param inputManager менеджер ввода
     * @param ticketFormReader читатель формы
     * @param ticketFactory фабрика билетов
     * @param history история команд
     * @param logger логгер
     */
    public CommandContext(
        CollectionService collectionService,
        CsvStorage csvStorage,
        Path dataFile,
        InputManager inputManager,
        TicketFormReader ticketFormReader,
        TicketFactory ticketFactory,
        CommandHistory history,
        AppLogger logger
    ) {
        this.collectionService = collectionService;
        this.csvStorage = csvStorage;
        this.dataFile = dataFile;
        this.inputManager = inputManager;
        this.ticketFormReader = ticketFormReader;
        this.ticketFactory = ticketFactory;
        this.history = history;
        this.logger = logger;
    }

    public CollectionService collectionService() {
        return collectionService;
    }

    public CsvStorage csvStorage() {
        return csvStorage;
    }

    public Path dataFile() {
        return dataFile;
    }

    public InputManager inputManager() {
        return inputManager;
    }

    public TicketFormReader ticketFormReader() {
        return ticketFormReader;
    }

    public TicketFactory ticketFactory() {
        return ticketFactory;
    }

    public CommandHistory history() {
        return history;
    }

    public AppLogger logger() {
        return logger;
    }

    public CommandRegistry registry() {
        return registry;
    }

    public void setRegistry(CommandRegistry registry) {
        this.registry = registry;
    }

    public ReadLine currentLine() {
        return currentLine;
    }

    public void setCurrentLine(ReadLine currentLine) {
        this.currentLine = currentLine;
    }
}
