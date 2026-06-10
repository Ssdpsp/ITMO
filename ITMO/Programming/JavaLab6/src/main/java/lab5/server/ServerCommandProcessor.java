package lab5.server;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lab5.application.CollectionService;
import lab5.application.CommandHistory;
import lab5.application.CommandResult;
import lab5.domain.model.Ticket;
import lab5.domain.model.TicketDraft;
import lab5.domain.service.TicketFactory;
import lab5.infrastructure.storage.CsvStorage;
import lab5.infrastructure.storage.SaveReport;
import lab5.shared.CommandCode;
import lab5.shared.CommandPacket;

/**
 * Выполняет команды на стороне сервера.
 */
public class ServerCommandProcessor {
    private CollectionService collectionService;
    private CsvStorage csvStorage;
    private Path dataFile;
    private TicketFactory ticketFactory;
    private CommandHistory history;
    private Logger logger;

    public ServerCommandProcessor(
        CollectionService collectionService,
        CsvStorage csvStorage,
        Path dataFile,
        TicketFactory ticketFactory,
        CommandHistory history,
        Logger logger
    ) {
        this.collectionService = collectionService;
        this.csvStorage = csvStorage;
        this.dataFile = dataFile;
        this.ticketFactory = ticketFactory;
        this.history = history;
        this.logger = logger;
    }

    public CommandResult process(CommandPacket packet) {
        if (packet == null || packet.getCommand() == null) {
            return CommandResult.error("Пустой запрос.");
        }
        history.add(packet.getCommand().commandName());
        try {
            return switch (packet.getCommand()) {
                case HELP -> help(packet);
                case INFO -> info(packet);
                case SHOW -> show(packet);
                case ADD -> add(packet);
                case UPDATE -> update(packet);
                case REMOVE_BY_ID -> removeById(packet);
                case CLEAR -> clear(packet);
                case SAVE -> CommandResult.error("Команда save доступна только в консоли сервера.");
                case EXECUTE_SCRIPT -> CommandResult.error("execute_script выполняется на клиенте.");
                case EXIT -> CommandResult.error("exit завершает клиент, не сервер.");
                case ADD_IF_MAX -> addIfMax(packet);
                case REMOVE_GREATER -> removeGreater(packet);
                case REMOVE_LOWER -> removeLower(packet);
                case GROUP_COUNTING_BY_CREATION_DATE -> groupCountingByCreationDate(packet);
                case FILTER_CONTAINS_NAME -> filterContainsName(packet);
                case FILTER_LESS_THAN_REFUNDABLE -> filterLessThanRefundable(packet);
                case HISTORY -> history(packet);
            };
        } catch (Exception exception) {
            logger.log(Level.WARNING, "Ошибка выполнения команды " + packet.getCommand(), exception);
            return CommandResult.error("Ошибка выполнения команды: " + exception.getMessage());
        }
    }

    public CommandResult saveFromServer() {
        SaveReport report = csvStorage.save(dataFile, collectionService.snapshotSortedByName());
        if (report.isSuccess()) {
            logger.info("Сохранение коллекции: " + report.getMessage());
            return CommandResult.success(report.getMessage());
        }
        logger.warning("Ошибка сохранения коллекции: " + report.getMessage());
        return CommandResult.error(report.getMessage());
    }

    private CommandResult help(CommandPacket packet) {
        CommandResult error = requireNoArgs(packet);
        if (error != null) {
            return error;
        }
        String text = """
            Доступные команды клиента:
            help : вывести справку по командам
            info : вывести информацию о коллекции
            show : вывести элементы коллекции, отсортированные по name
            add {element} : добавить новый элемент
            update id {element} : обновить элемент по id
            remove_by_id id : удалить элемент по id
            clear : очистить коллекцию
            execute_script file_name : выполнить команды из файла на клиенте
            exit : завершить клиент
            add_if_max {element} : добавить элемент, если он больше максимального
            remove_greater {element} : удалить элементы больше заданного
            remove_lower {element} : удалить элементы меньше заданного
            group_counting_by_creation_date : сгруппировать по creationDate
            filter_contains_name name : вывести элементы, name которых содержит строку
            filter_less_than_refundable refundable : вывести элементы с refundable меньше заданного
            history : показать последние команды

            Команда save не отправляется клиентом и доступна только в консоли сервера.
            """;
        return CommandResult.success(text.trim());
    }

    private CommandResult info(CommandPacket packet) {
        CommandResult error = requireNoArgs(packet);
        if (error != null) {
            return error;
        }
        String message = "Тип коллекции: " + collectionService.getCollectionType()
            + System.lineSeparator()
            + "Дата инициализации: " + collectionService.getInitializationDate()
            + System.lineSeparator()
            + "Количество элементов: " + collectionService.size();
        return CommandResult.success(message);
    }

    private CommandResult show(CommandPacket packet) {
        CommandResult error = requireNoArgs(packet);
        if (error != null) {
            return error;
        }
        LinkedHashSet<Ticket> tickets = collectionService.snapshotSortedByName();
        if (tickets.isEmpty()) {
            return CommandResult.success("Коллекция пуста.", tickets);
        }
        return CommandResult.success(ticketsToString(tickets), tickets);
    }

    private CommandResult add(CommandPacket packet) {
        TicketDraft draft = requireElement(packet);
        if (draft == null) {
            return CommandResult.error("Для команды add нужен объект Ticket.");
        }
        Ticket ticket = ticketFactory.createNew(draft);
        if (!collectionService.add(ticket)) {
            return CommandResult.error("Элемент с таким id уже существует.");
        }
        return CommandResult.success("Элемент добавлен. id=" + ticket.getId());
    }

    private CommandResult update(CommandPacket packet) {
        Long id = (Long) packet.argumentValue("id", Long.class);
        if (id == null || id <= 0 || packet.getElement() == null) {
            return CommandResult.error("Синтаксис: update id {element}");
        }
        Optional<Ticket> existing = collectionService.findById(id);
        if (existing.isEmpty()) {
            return CommandResult.error("Элемент с id=" + id + " не найден.");
        }
        Ticket updated = ticketFactory.createForUpdate(existing.get(), packet.getElement());
        boolean ok = collectionService.updateById(id, updated);
        if (!ok) {
            return CommandResult.error("Не удалось обновить элемент с id=" + id + ".");
        }
        return CommandResult.success("Элемент с id=" + id + " обновлен.");
    }

    private CommandResult removeById(CommandPacket packet) {
        Long id = (Long) packet.argumentValue("id", Long.class);
        if (id == null || id <= 0) {
            return CommandResult.error("Синтаксис: remove_by_id id");
        }
        boolean removed = collectionService.removeById(id);
        if (!removed) {
            return CommandResult.error("Элемент с id=" + id + " не найден.");
        }
        return CommandResult.success("Элемент с id=" + id + " удален.");
    }

    private CommandResult clear(CommandPacket packet) {
        CommandResult error = requireNoArgs(packet);
        if (error != null) {
            return error;
        }
        collectionService.clear();
        return CommandResult.success("Коллекция очищена.");
    }

    private CommandResult addIfMax(CommandPacket packet) {
        TicketDraft draft = requireElement(packet);
        if (draft == null) {
            return CommandResult.error("Для команды add_if_max нужен объект Ticket.");
        }
        Ticket reference = ticketFactory.createTransient(draft);
        Optional<Ticket> max = collectionService.max();
        if (max.isPresent() && reference.compareTo(max.get()) <= 0) {
            return CommandResult.success("Элемент не добавлен: он не превышает максимальный.");
        }
        Ticket ticket = ticketFactory.createNew(draft);
        collectionService.add(ticket);
        return CommandResult.success("Элемент добавлен как новый максимум. id=" + ticket.getId());
    }

    private CommandResult removeGreater(CommandPacket packet) {
        TicketDraft draft = requireElement(packet);
        if (draft == null) {
            return CommandResult.error("Для команды remove_greater нужен объект Ticket.");
        }
        Ticket reference = ticketFactory.createTransient(draft);
        int removed = collectionService.removeGreater(reference);
        return CommandResult.success("Удалено элементов: " + removed + ".");
    }

    private CommandResult removeLower(CommandPacket packet) {
        TicketDraft draft = requireElement(packet);
        if (draft == null) {
            return CommandResult.error("Для команды remove_lower нужен объект Ticket.");
        }
        Ticket reference = ticketFactory.createTransient(draft);
        int removed = collectionService.removeLower(reference);
        return CommandResult.success("Удалено элементов: " + removed + ".");
    }

    private CommandResult groupCountingByCreationDate(CommandPacket packet) {
        CommandResult error = requireNoArgs(packet);
        if (error != null) {
            return error;
        }
        Map<ZonedDateTime, Long> grouped = collectionService.countByCreationDate();
        if (grouped.isEmpty()) {
            return CommandResult.success("Коллекция пуста.", grouped);
        }
        String message = grouped.entrySet().stream()
            .map(entry -> entry.getKey() + " -> " + entry.getValue())
            .collect(Collectors.joining(System.lineSeparator()));
        return CommandResult.success(message, grouped);
    }

    private CommandResult filterContainsName(CommandPacket packet) {
        String substring = (String) packet.argumentValue("substring", String.class);
        if (substring == null || substring.isBlank()) {
            return CommandResult.error("Синтаксис: filter_contains_name name");
        }
        LinkedHashSet<Ticket> filtered = collectionService.sortByName(collectionService.filterContainsName(substring));
        if (filtered.isEmpty()) {
            return CommandResult.success("Подходящих элементов не найдено.", filtered);
        }
        return CommandResult.success(ticketsToString(filtered), filtered);
    }

    private CommandResult filterLessThanRefundable(CommandPacket packet) {
        Boolean refundable = (Boolean) packet.argumentValue("refundable", Boolean.class);
        if (refundable == null) {
            return CommandResult.error("Синтаксис: filter_less_than_refundable refundable");
        }
        LinkedHashSet<Ticket> filtered = collectionService.sortByName(collectionService.filterLessThanRefundable(refundable));
        if (filtered.isEmpty()) {
            return CommandResult.success("Подходящих элементов не найдено.", filtered);
        }
        return CommandResult.success(ticketsToString(filtered), filtered);
    }

    private CommandResult history(CommandPacket packet) {
        CommandResult error = requireNoArgs(packet);
        if (error != null) {
            return error;
        }
        List<String> list = history.list();
        if (list.isEmpty()) {
            return CommandResult.success("История пуста.", list);
        }
        String message = IntStream.range(0, list.size())
            .mapToObj(index -> (index + 1) + ". " + list.get(index))
            .collect(Collectors.joining(System.lineSeparator()));
        return CommandResult.success(message, list);
    }

    private CommandResult requireNoArgs(CommandPacket packet) {
        if (!packet.getArguments().isEmpty() || packet.getElement() != null) {
            return CommandResult.error("Команда " + packet.getCommand().commandName() + " не принимает аргументы.");
        }
        return null;
    }

    private TicketDraft requireElement(CommandPacket packet) {
        if (!packet.getArguments().isEmpty()) {
            return null;
        }
        return packet.getElement();
    }

    private String ticketsToString(LinkedHashSet<Ticket> tickets) {
        return tickets.stream()
            .map(Ticket::toString)
            .collect(Collectors.joining(System.lineSeparator()));
    }
}
