package lab5.presentation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import lab5.domain.model.Coordinates;
import lab5.domain.model.EventDraft;
import lab5.domain.model.TicketDraft;
import lab5.domain.model.TicketType;
import lab5.domain.validation.ValidationResult;
import lab5.domain.validation.ValidationService;
import lab5.infrastructure.input.InputManager;
import lab5.infrastructure.input.InputMode;
import lab5.infrastructure.input.ReadLine;

/**
 * Читает составные объекты Ticket из текущего источника ввода.
 */
public class TicketFormReader {
    private InputManager inputManager;
    private ConsolePresenter presenter;
    private ValidationService validationService;

    /**
     * Создает читатель.
     *
     * @param inputManager менеджер ввода
     * @param presenter объект вывода
     * @param validationService сервис валидации
     */
    public TicketFormReader(
        InputManager inputManager,
        ConsolePresenter presenter,
        ValidationService validationService
    ) {
        this.inputManager = inputManager;
        this.presenter = presenter;
        this.validationService = validationService;
    }

    /**
     * Читает полный черновик билета с поддержкой back/skip/cancel.
     *
     * @return результат чтения
     */
    public ObjectReadResult readTicketDraft() {
        String name = null;
        Integer coordinatesX = null;
        Double coordinatesY = null;
        Integer price = null;
        Boolean refundable = null;
        TicketType type = null;
        String eventName = null;
        LocalDateTime eventDate = null;
        String eventDescription = null;

        int step = 0;
        while (step < 9) {
            InputMode mode = inputManager.currentMode();
            switch (step) {
                case 0 -> {
                    ReadFieldResult result = readField(
                        mode,
                        "Введите поле name (не пустая строка). Команды: back/cancel.",
                        false
                    );
                    if (result.isRetry()) {
                        continue;
                    }
                    if (result.isFailed()) {
                        return ObjectReadResult.failed(result.failReason());
                    }
                    if (result.isCanceled()) {
                        return ObjectReadResult.canceled();
                    }
                    if (result.isBack()) {
                        presenter.printError("Вы уже на первом поле.");
                        continue;
                    }
                    ValidationResult validation = validationService.validateTicketName(result.value());
                    if (!validation.isValid()) {
                        presenter.printError(errorWithSuggestion(validation));
                        continue;
                    }
                    name = result.value();
                    step += 1;
                }
                case 1 -> {
                    ReadFieldResult result = readField(
                        mode,
                        "Введите поле coordinates.x (int, максимум 222). Команды: back/cancel.",
                        false
                    );
                    if (result.isRetry()) {
                        continue;
                    }
                    if (result.isFailed()) {
                        return ObjectReadResult.failed(result.failReason());
                    }
                    if (result.isCanceled()) {
                        return ObjectReadResult.canceled();
                    }
                    if (result.isBack()) {
                        step -= 1;
                        continue;
                    }
                    Integer parsed = parseInt(result.value(), "Ожидалось целое число для coordinates.x.");
                    if (parsed == null) {
                        continue;
                    }
                    ValidationResult validation = validationService.validateCoordinatesX(parsed);
                    if (!validation.isValid()) {
                        presenter.printError(errorWithSuggestion(validation));
                        continue;
                    }
                    coordinatesX = parsed;
                    step += 1;
                }
                case 2 -> {
                    ReadFieldResult result = readField(
                        mode,
                        "Введите поле coordinates.y (double, значение > -234). Команды: back/cancel.",
                        false
                    );
                    if (result.isRetry()) {
                        continue;
                    }
                    if (result.isFailed()) {
                        return ObjectReadResult.failed(result.failReason());
                    }
                    if (result.isCanceled()) {
                        return ObjectReadResult.canceled();
                    }
                    if (result.isBack()) {
                        step -= 1;
                        continue;
                    }
                    Double parsed = parseDouble(result.value(), "Ожидалось число double для coordinates.y.");
                    if (parsed == null) {
                        continue;
                    }
                    ValidationResult validation = validationService.validateCoordinatesY(parsed);
                    if (!validation.isValid()) {
                        presenter.printError(errorWithSuggestion(validation));
                        continue;
                    }
                    coordinatesY = parsed;
                    step += 1;
                }
                case 3 -> {
                    ReadFieldResult result = readField(
                        mode,
                        "Введите поле price (int, значение > 0). Команды: back/cancel.",
                        false
                    );
                    if (result.isRetry()) {
                        continue;
                    }
                    if (result.isFailed()) {
                        return ObjectReadResult.failed(result.failReason());
                    }
                    if (result.isCanceled()) {
                        return ObjectReadResult.canceled();
                    }
                    if (result.isBack()) {
                        step -= 1;
                        continue;
                    }
                    Integer parsed = parseInt(result.value(), "Ожидалось целое число для price.");
                    if (parsed == null) {
                        continue;
                    }
                    ValidationResult validation = validationService.validatePrice(parsed);
                    if (!validation.isValid()) {
                        presenter.printError(errorWithSuggestion(validation));
                        continue;
                    }
                    price = parsed;
                    step += 1;
                }
                case 4 -> {
                    ReadFieldResult result = readField(
                        mode,
                        "Введите поле refundable (true/false, 1/0, yes/no). Команды: back/cancel.",
                        false
                    );
                    if (result.isRetry()) {
                        continue;
                    }
                    if (result.isFailed()) {
                        return ObjectReadResult.failed(result.failReason());
                    }
                    if (result.isCanceled()) {
                        return ObjectReadResult.canceled();
                    }
                    if (result.isBack()) {
                        step -= 1;
                        continue;
                    }
                    Optional<Boolean> parsed = parseBoolean(result.value());
                    if (parsed.isEmpty()) {
                        presenter.printError(
                            "Неверное значение refundable. Допустимо: true/false, 1/0, yes/no."
                        );
                        continue;
                    }
                    ValidationResult validation = validationService.validateRefundable(parsed.get());
                    if (!validation.isValid()) {
                        presenter.printError(errorWithSuggestion(validation));
                        continue;
                    }
                    refundable = parsed.get();
                    step += 1;
                }
                case 5 -> {
                    ReadFieldResult result = readField(
                        mode,
                        "Введите поле type (nullable). Допустимо: "
                            + enumHints()
                            + ". Пустая строка/skip => null. Команды: back/skip/cancel.",
                        true
                    );
                    if (result.isRetry()) {
                        continue;
                    }
                    if (result.isFailed()) {
                        return ObjectReadResult.failed(result.failReason());
                    }
                    if (result.isCanceled()) {
                        return ObjectReadResult.canceled();
                    }
                    if (result.isBack()) {
                        step -= 1;
                        continue;
                    }
                    if (result.isSkipped()) {
                        type = null;
                        step += 1;
                        continue;
                    }
                    TicketType parsed = parseTicketType(result.value());
                    if (parsed == null) {
                        presenter.printError(
                            "Неверное значение enum. Введите имя константы или индекс: " + enumHints() + "."
                        );
                        continue;
                    }
                    type = parsed;
                    step += 1;
                }
                case 6 -> {
                    ReadFieldResult result = readField(
                        mode,
                        "Введите поле event.name (nullable event). "
                            + "Пустая строка/skip => event = null. Команды: back/skip/cancel.",
                        true
                    );
                    if (result.isRetry()) {
                        continue;
                    }
                    if (result.isFailed()) {
                        return ObjectReadResult.failed(result.failReason());
                    }
                    if (result.isCanceled()) {
                        return ObjectReadResult.canceled();
                    }
                    if (result.isBack()) {
                        step -= 1;
                        continue;
                    }
                    if (result.isSkipped()) {
                        eventName = null;
                        eventDate = null;
                        eventDescription = null;
                        step = 9;
                        continue;
                    }
                    ValidationResult validation = validationService.validateEventName(result.value());
                    if (!validation.isValid()) {
                        presenter.printError(errorWithSuggestion(validation));
                        continue;
                    }
                    eventName = result.value();
                    step += 1;
                }
                case 7 -> {
                    ReadFieldResult result = readField(
                        mode,
                        "Введите поле event.date в формате LocalDateTime (например 2026-03-01T12:30:00). "
                            + "Команды: back/cancel.",
                        false
                    );
                    if (result.isRetry()) {
                        continue;
                    }
                    if (result.isFailed()) {
                        return ObjectReadResult.failed(result.failReason());
                    }
                    if (result.isCanceled()) {
                        return ObjectReadResult.canceled();
                    }
                    if (result.isBack()) {
                        step -= 1;
                        continue;
                    }
                    LocalDateTime parsed = parseLocalDateTime(result.value());
                    if (parsed == null) {
                        presenter.printError("Неверный формат даты. Используйте LocalDateTime: yyyy-MM-ddTHH:mm:ss.");
                        continue;
                    }
                    ValidationResult validation = validationService.validateEventDate(parsed);
                    if (!validation.isValid()) {
                        presenter.printError(errorWithSuggestion(validation));
                        continue;
                    }
                    eventDate = parsed;
                    step += 1;
                }
                case 8 -> {
                    ReadFieldResult result = readField(
                        mode,
                        "Введите поле event.description (не пустая строка). Команды: back/cancel.",
                        false
                    );
                    if (result.isRetry()) {
                        continue;
                    }
                    if (result.isFailed()) {
                        return ObjectReadResult.failed(result.failReason());
                    }
                    if (result.isCanceled()) {
                        return ObjectReadResult.canceled();
                    }
                    if (result.isBack()) {
                        step -= 1;
                        continue;
                    }
                    ValidationResult validation = validationService.validateEventDescription(result.value());
                    if (!validation.isValid()) {
                        presenter.printError(errorWithSuggestion(validation));
                        continue;
                    }
                    eventDescription = result.value();
                    step += 1;
                }
                default -> throw new IllegalStateException("Unexpected step: " + step);
            }
        }

        EventDraft eventDraft = eventName == null ? null : new EventDraft(eventName, eventDate, eventDescription);
        TicketDraft ticketDraft = new TicketDraft(
            name,
            new Coordinates(coordinatesX, coordinatesY),
            price,
            refundable,
            type,
            eventDraft
        );
        return ObjectReadResult.success(ticketDraft);
    }

    /**
     * Разбирает пользовательский ввод логического значения.
     *
     * @param raw исходное значение
     * @return разобранное логическое значение, если это возможно
     */
    public Optional<Boolean> parseBoolean(String raw) {
        if (raw == null) {
            return Optional.empty();
        }
        String normalized = raw.trim().toLowerCase();
        return switch (normalized) {
            case "true", "1", "yes", "y", "да", "д" -> Optional.of(Boolean.TRUE);
            case "false", "0", "no", "n", "нет", "н" -> Optional.of(Boolean.FALSE);
            default -> Optional.empty();
        };
    }

    private ReadFieldResult readField(InputMode mode, String prompt, boolean nullable) {
        presenter.printFieldPrompt(mode, prompt);
        ReadLine line;
        try {
            line = inputManager.readLine();
        } catch (IOException exception) {
            return ReadFieldResult.failed("Ошибка чтения ввода: " + exception.getMessage());
        }
        if (line == null) {
            return ReadFieldResult.failed("Ввод завершился до заполнения объекта.");
        }
        String value = line.text() == null ? "" : line.text().trim();
        if ("cancel".equalsIgnoreCase(value)) {
            return ReadFieldResult.canceled();
        }
        if ("back".equalsIgnoreCase(value)) {
            return ReadFieldResult.back();
        }
        if ("skip".equalsIgnoreCase(value)) {
            if (!nullable) {
                presenter.printError("Это поле не допускает null. Введите значение или cancel.");
                return ReadFieldResult.retry();
            }
            return ReadFieldResult.skipped();
        }
        if (value.isEmpty()) {
            if (!nullable) {
                presenter.printError("Пустая строка недопустима для этого поля. Введите корректное значение.");
                return ReadFieldResult.retry();
            }
            return ReadFieldResult.skipped();
        }
        return ReadFieldResult.value(value);
    }

    private String errorWithSuggestion(ValidationResult result) {
        String joined = String.join("; ", result.getErrors());
        return joined + " Введите значение в допустимых пределах.";
    }

    private Integer parseInt(String raw, String errorMessage) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException exception) {
            presenter.printError(errorMessage);
            return null;
        }
    }

    private Double parseDouble(String raw, String errorMessage) {
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException exception) {
            presenter.printError(errorMessage);
            return null;
        }
    }

    private LocalDateTime parseLocalDateTime(String raw) {
        try {
            return LocalDateTime.parse(raw);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    private TicketType parseTicketType(String raw) {
        String value = raw.trim();
        if (value.matches("\\d+")) {
            int idx = Integer.parseInt(value);
            TicketType[] values = TicketType.values();
            if (idx >= 0 && idx < values.length) {
                return values[idx];
            }
            return null;
        }
        try {
            return TicketType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private String enumHints() {
        TicketType[] values = TicketType.values();
        return Arrays.stream(values)
            .map(type -> type.ordinal() + "=" + type.name())
            .collect(Collectors.joining(", "));
    }

    private static class ReadFieldResult {
        private String value;
        private boolean back;
        private boolean skipped;
        private boolean canceled;
        private boolean failed;
        private String failReason;

        private ReadFieldResult(
            String value,
            boolean back,
            boolean skipped,
            boolean canceled,
            boolean failed,
            String failReason
        ) {
            this.value = value;
            this.back = back;
            this.skipped = skipped;
            this.canceled = canceled;
            this.failed = failed;
            this.failReason = failReason;
        }

        private static ReadFieldResult value(String value) {
            return new ReadFieldResult(value, false, false, false, false, null);
        }

        private static ReadFieldResult back() {
            return new ReadFieldResult(null, true, false, false, false, null);
        }

        private static ReadFieldResult skipped() {
            return new ReadFieldResult(null, false, true, false, false, null);
        }

        private static ReadFieldResult canceled() {
            return new ReadFieldResult(null, false, false, true, false, null);
        }

        private static ReadFieldResult retry() {
            return new ReadFieldResult(null, false, false, false, false, null);
        }

        private static ReadFieldResult failed(String reason) {
            return new ReadFieldResult(null, false, false, false, true, reason);
        }

        private boolean isBack() {
            return back;
        }

        private boolean isSkipped() {
            return skipped;
        }

        private boolean isCanceled() {
            return canceled;
        }

        private boolean isRetry() {
            return !back && !skipped && !canceled && !failed && value == null;
        }

        private boolean isFailed() {
            return failed;
        }

        private String failReason() {
            return failReason;
        }

        private String value() {
            return value;
        }
    }
}
