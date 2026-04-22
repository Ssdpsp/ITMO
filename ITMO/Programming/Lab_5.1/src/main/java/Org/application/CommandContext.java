package Org.application;

import java.nio.file.Path;
import Org.domain.service.TicketFactory;
import Org.infrastructure.input.InputManager;
import Org.infrastructure.input.ReadLine;
import Org.infrastructure.logging.AppLogger;
import Org.infrastructure.storage.CsvStorage;
import Org.presentation.TicketFormReader;

/**
 * Контекст выполненияя, передавемый команде.
 */
public final class CommandContext {
    private final CollectionService collectionService;
    private final CsvStorage csvStorage;
    private final Path dataFile;
    private final InputManager inputManager;
    private final TicketFormReader ticketFormReader;
    private final TicketFactory ticketFactory;
    private final CommandHistory history;
    private final AppLogger logger;
    private CommandRegistry registry;
    private ReadLine currentLine;

    /**
     * Создание контекста.
     *
     * @param collectionService collection service
     * @param csvStorage storage
     * @param dataFile data file
     * @param inputManager input manager
     * @param ticketFormReader form reader
     * @param ticketFactory ticket factory
     * @param history command history
     * @param logger logger
     */
    public CommandContext(
            final CollectionService collectionService,
            final CsvStorage csvStorage,
            final Path dataFile,
            final InputManager inputManager,
            final TicketFormReader ticketFormReader,
            final TicketFactory ticketFactory,
            final CommandHistory history,
            final AppLogger logger
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

    public void setRegistry(final CommandRegistry registry) {
        this.registry = registry;
    }

    public ReadLine currentLine() {
        return currentLine;
    }

    public void setCurrentLine(final ReadLine currentLine) {
        this.currentLine = currentLine;
    }
}