package lab5.infrastructure.storage;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lab5.domain.model.Coordinates;
import lab5.domain.model.Event;
import lab5.domain.model.Ticket;
import lab5.domain.model.TicketType;
import lab5.domain.validation.ValidationResult;
import lab5.domain.validation.ValidationService;
import lab5.infrastructure.logging.AppLogger;

/**
 * CSV-хранилище коллекции билетов.
 */
public class CsvStorage {
    private static String HEADER =
        "id,name,coordinates_x,coordinates_y,creation_date,price,refundable,type,event_id,event_name,event_date,event_description";

    private ValidationService validationService;
    private AppLogger logger;

    /**
     * Создает хранилище.
     *
     * @param validationService валидатор
     * @param logger логгер
     */
    public CsvStorage(ValidationService validationService, AppLogger logger) {
        this.validationService = validationService;
        this.logger = logger;
    }

    /**
     * Загружает билеты из CSV.
     *
     * @param filePath путь к файлу
     * @return отчет о загрузке
     */
    public LoadReport load(Path filePath) {
        if (filePath == null) {
            return new LoadReport(new LinkedHashSet<>(), List.of(), "Файл не задан.", true);
        }
        if (!Files.exists(filePath)) {
            return new LoadReport(
                new LinkedHashSet<>(),
                List.of(),
                "Файл не найден: " + filePath.toAbsolutePath(),
                false
            );
        }
        if (Files.isDirectory(filePath)) {
            return new LoadReport(
                new LinkedHashSet<>(),
                List.of(),
                "Указанный путь является каталогом: " + filePath.toAbsolutePath(),
                true
            );
        }
        if (!Files.isReadable(filePath)) {
            return new LoadReport(
                new LinkedHashSet<>(),
                List.of(),
                "Нет прав на чтение файла: " + filePath.toAbsolutePath(),
                true
            );
        }

        LinkedHashSet<Ticket> tickets = new LinkedHashSet<>();
        List<ProblemLine> problems = new ArrayList<>();
        Set<Long> ticketIds = new HashSet<>();
        Set<Integer> eventIds = new HashSet<>();

        try (
            BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(filePath));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                return new LoadReport(tickets, problems, "Файл пуст. Загружена пустая коллекция.", false);
            }
            firstLine = stripUtf8Bom(firstLine);

            int lineNumber = 1;
            if (!isHeader(firstLine)) {
                parseAndAppend(firstLine, lineNumber, tickets, problems, ticketIds, eventIds);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber += 1;
                parseAndAppend(line, lineNumber, tickets, problems, ticketIds, eventIds);
            }
        } catch (AccessDeniedException exception) {
            return new LoadReport(
                tickets,
                problems,
                "Нет прав доступа к файлу: " + filePath.toAbsolutePath(),
                true
            );
        } catch (IOException exception) {
            logger.logError("Ошибка чтения CSV: " + filePath.toAbsolutePath(), exception);
            return new LoadReport(
                tickets,
                problems,
                "Ошибка чтения файла: " + exception.getMessage(),
                true
            );
        }

        String message = problems.isEmpty()
            ? "Загрузка завершена: " + tickets.size() + " записей."
            : "Загрузка завершена частично: " + tickets.size() + " записей, ошибок строк: " + problems.size() + ".";
        return new LoadReport(tickets, problems, message, false);
    }

    /**
     * Сохраняет билеты в CSV.
     *
     * @param filePath путь к файлу
     * @param tickets билеты
     * @return отчет о сохранении
     */
    public SaveReport save(Path filePath, LinkedHashSet<Ticket> tickets) {
        if (filePath == null) {
            return new SaveReport(false, "Файл не задан.");
        }
        if (Files.exists(filePath) && Files.isDirectory(filePath)) {
            return new SaveReport(false, "Нельзя сохранить: путь указывает на каталог.");
        }
        try {
            Path parent = filePath.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException exception) {
            return new SaveReport(false, "Не удалось создать директорию для файла: " + exception.getMessage());
        }

        try (FileWriter writer = new FileWriter(filePath.toFile(), StandardCharsets.UTF_8, false)) {
            writer.write(HEADER);
            writer.write(System.lineSeparator());
            for (Ticket ticket : tickets) {
                writer.write(toCsvLine(ticket));
                writer.write(System.lineSeparator());
            }
            writer.flush();
            return new SaveReport(true, "Коллекция сохранена в файл: " + filePath.toAbsolutePath());
        } catch (IOException exception) {
            logger.logError("Ошибка записи CSV: " + filePath.toAbsolutePath(), exception);
            return new SaveReport(false, "Ошибка записи файла: " + exception.getMessage());
        }
    }

    private void parseAndAppend(
        String line,
        int lineNumber,
        LinkedHashSet<Ticket> tickets,
        List<ProblemLine> problems,
        Set<Long> ticketIds,
        Set<Integer> eventIds
    ) {
        try {
            List<String> values = parseCsvLine(line);
            if (values.size() != 12) {
                problems.add(new ProblemLine(lineNumber, line, "Ожидалось 12 колонок, получено: " + values.size()));
                return;
            }
            Ticket ticket = parseTicket(values);
            ValidationResult result = validationService.validateTicket(ticket);
            if (!result.isValid()) {
                problems.add(new ProblemLine(lineNumber, line, String.join("; ", result.getErrors())));
                return;
            }
            if (!ticketIds.add(ticket.getId())) {
                problems.add(new ProblemLine(lineNumber, line, "Дублирующийся ticket.id: " + ticket.getId()));
                return;
            }
            if (ticket.getEvent() != null && !eventIds.add(ticket.getEvent().getId())) {
                problems.add(new ProblemLine(lineNumber, line, "Дублирующийся event.id: " + ticket.getEvent().getId()));
                return;
            }
            tickets.add(ticket);
        } catch (IllegalArgumentException exception) {
            problems.add(new ProblemLine(lineNumber, line, exception.getMessage()));
        }
    }

    private Ticket parseTicket(List<String> values) {
        long id = parseLong(values.get(0), "id");
        String name = values.get(1);
        int coordinatesX = parseInt(values.get(2), "coordinates_x");
        double coordinatesY = parseDouble(values.get(3), "coordinates_y");
        ZonedDateTime creationDate = parseZonedDateTime(values.get(4), "creation_date");
        int price = parseInt(values.get(5), "price");
        Boolean refundable = parseBoolean(values.get(6), "refundable");
        TicketType type = parseTicketTypeNullable(values.get(7), "type");

        Event event = parseEvent(
            values.get(8),
            values.get(9),
            values.get(10),
            values.get(11)
        );
        return new Ticket(
            id,
            name,
            new Coordinates(coordinatesX, coordinatesY),
            creationDate,
            price,
            refundable,
            type,
            event
        );
    }

    private Event parseEvent(
        String eventIdRaw,
        String eventNameRaw,
        String eventDateRaw,
        String eventDescriptionRaw
    ) {
        boolean allEmpty = eventIdRaw.isBlank()
            && eventNameRaw.isBlank()
            && eventDateRaw.isBlank()
            && eventDescriptionRaw.isBlank();
        if (allEmpty) {
            return null;
        }
        int eventId = parseInt(eventIdRaw, "event_id");
        String eventName = eventNameRaw;
        LocalDateTime eventDate = parseLocalDateTime(eventDateRaw, "event_date");
        String eventDescription = eventDescriptionRaw;
        return new Event(eventId, eventName, eventDate, eventDescription);
    }

    private boolean isHeader(String line) {
        return HEADER.equalsIgnoreCase(line.trim());
    }

    private String stripUtf8Bom(String line) {
        if (line != null && !line.isEmpty() && line.charAt(0) == '\uFEFF') {
            return line.substring(1);
        }
        return line;
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        int index = 0;
        while (index < line.length()) {
            char currentChar = line.charAt(index);
            if (currentChar == '"') {
                if (inQuotes && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    current.append('"');
                    index += 1;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (currentChar == ',' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(currentChar);
            }
            index += 1;
        }
        values.add(current.toString());
        return values;
    }

    private String toCsvLine(Ticket ticket) {
        Event event = ticket.getEvent();
        return String.join(
            ",",
            esc(Long.toString(ticket.getId())),
            esc(ticket.getName()),
            esc(Integer.toString(ticket.getCoordinates().getX())),
            esc(Double.toString(ticket.getCoordinates().getY())),
            esc(ticket.getCreationDate().toString()),
            esc(Integer.toString(ticket.getPrice())),
            esc(ticket.getRefundable().toString()),
            esc(ticket.getType() == null ? "" : ticket.getType().name()),
            esc(event == null ? "" : Integer.toString(event.getId())),
            esc(event == null ? "" : event.getName()),
            esc(event == null ? "" : event.getDate().toString()),
            esc(event == null ? "" : event.getDescription())
        );
    }

    private String esc(String value) {
        if (value == null) {
            return "";
        }
        boolean needsQuote = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r");
        String escaped = value.replace("\"", "\"\"");
        return needsQuote ? "\"" + escaped + "\"" : escaped;
    }

    private long parseLong(String raw, String field) {
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Поле " + field + ": ожидалось целое long.");
        }
    }

    private int parseInt(String raw, String field) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Поле " + field + ": ожидалось целое int.");
        }
    }

    private double parseDouble(String raw, String field) {
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Поле " + field + ": ожидалось число double.");
        }
    }

    private Boolean parseBoolean(String raw, String field) {
        if ("true".equalsIgnoreCase(raw)) {
            return true;
        }
        if ("false".equalsIgnoreCase(raw)) {
            return false;
        }
        throw new IllegalArgumentException("Поле " + field + ": ожидалось true/false.");
    }

    private TicketType parseTicketTypeNullable(String raw, String field) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return TicketType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Поле " + field + ": неизвестное enum значение.");
        }
    }

    private ZonedDateTime parseZonedDateTime(String raw, String field) {
        try {
            return ZonedDateTime.parse(raw);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Поле " + field + ": неверный формат ZonedDateTime.");
        }
    }

    private LocalDateTime parseLocalDateTime(String raw, String field) {
        try {
            return LocalDateTime.parse(raw);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Поле " + field + ": неверный формат LocalDateTime.");
        }
    }
}
