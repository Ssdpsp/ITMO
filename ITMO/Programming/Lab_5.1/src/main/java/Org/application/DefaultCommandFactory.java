package Org.application;

import java.io.IOException;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import Org.domain.model.Ticket;
import Org.domain.model.TicketDraft;
import Org.infrastructure.storage.SaveReport;
import Org.presentation.ObjectReadResult;

/**
 * Создание команд.
 */
public final class DefaultCommandFactory implements CommandFactory {

    /**
     * Создание списка команд.
     *
     * @return commands
     */
    @Override
    public List<Command> createCommands() {
        List<Command> commands = new ArrayList<>();
        commands.add(help());
        commands.add(info());
        commands.add(show());
        commands.add(add());
        commands.add(update());
        commands.add(removeById());
        commands.add(clear());
        commands.add(save());
        commands.add(executeScript());
        commands.add(exit());
        commands.add(addIfMax());
        commands.add(removeGreater());
        commands.add(removeLower());
        commands.add(groupCountingByCreationDate());
        commands.add(filterContainsName());
        commands.add(filterLessThanRefundable());
        commands.add(history());
        return commands;
    }

    private Command help() {
        return new LambdaCommand(
                "help",
                "вывести справку по доступным командам",
                "help",
                "help",
                "Аргументы отсутствуют.",
                (request, context) -> {
                    if (hasArgs(request)) {
                        return CommandResult.error("Команда help не принимает аргументы.");
                    }
                    StringBuilder builder = new StringBuilder();
                    builder.append("Доступные команды:").append(System.lineSeparator());
                    for (Command command : context.registry().all()) {
                        builder.append("- ")
                                .append(command.name())
                                .append(" : ")
                                .append(command.description())
                                .append(System.lineSeparator());
                        builder.append("  Синтаксис: ").append(command.syntax()).append(System.lineSeparator());
                        builder.append("  Пример: ").append(command.example()).append(System.lineSeparator());
                        builder.append("  Правила: ").append(command.argumentRules()).append(System.lineSeparator());
                    }
                    builder.append(System.lineSeparator())
                            .append("Правила ввода {element}:").append(System.lineSeparator())
                            .append("1) Составной объект вводится по одному полю в строку.").append(System.lineSeparator())
                            .append("2) Служебные команды: back, skip (для nullable), cancel.").append(System.lineSeparator())
                            .append("3) Null можно задать пустой строкой для nullable полей.").append(System.lineSeparator())
                            .append("4) Enum: имя константы или индекс.");
                    return CommandResult.success(builder.toString());
                }
        );
    }

    private Command info() {
        return new LambdaCommand(
                "info",
                "вывести информацию о коллекции",
                "info",
                "info",
                "Аргументы отсутствуют.",
                (request, context) -> {
                    if (hasArgs(request)) {
                        return CommandResult.error("Команда info не принимает аргументы.");
                    }
                    String message = "Тип коллекции: " + context.collectionService().getCollectionType()
                            + System.lineSeparator()
                            + "Дата инициализации: " + context.collectionService().getInitializationDate()
                            + System.lineSeparator()
                            + "Количество элементов: " + context.collectionService().size()
                            + System.lineSeparator()
                            + "Текущий режим ввода: " + context.inputManager().currentMode();
                    return CommandResult.success(message);
                }
        );
    }

    private Command show() {
        return new LambdaCommand(
                "show",
                "вывести все элементы коллекции",
                "show",
                "show",
                "Аргументы отсутствуют.",
                (request, context) -> {
                    if (hasArgs(request)) {
                        return CommandResult.error("Команда show не принимает аргументы.");
                    }
                    LinkedHashSet<Ticket> tickets = context.collectionService().snapshot();
                    if (tickets.isEmpty()) {
                        return CommandResult.success("Коллекция пуста.");
                    }
                    String content = tickets.stream()
                            .map(Ticket::toString)
                            .collect(Collectors.joining(System.lineSeparator()));
                    return CommandResult.success(content, tickets);
                }
        );
    }

    private Command add() {
        return new LambdaCommand(
                "add",
                "добавить новый элемент в коллекцию",
                "add {element}",
                "add",
                "После имени команды введите поля Ticket по приглашениям.",
                (request, context) -> {
                    if (hasArgs(request)) {
                        return CommandResult.error("Команда add не принимает аргументы в строке.");
                    }
                    ObjectReadResult<TicketDraft> read = context.ticketFormReader().readTicketDraft();
                    if (read.isCanceled()) {
                        return CommandResult.error("Добавление отменено.");
                    }
                    if (read.error().isPresent()) {
                        return CommandResult.error(read.error().get());
                    }
                    Ticket ticket = context.ticketFactory().createNew(read.value().orElseThrow());
                    boolean added = context.collectionService().add(ticket);
                    if (!added) {
                        return CommandResult.error("Элемент с таким id уже существует.");
                    }
                    return CommandResult.success("Элемент добавлен. id=" + ticket.getId());
                }
        );
    }

    private Command update() {
        return new LambdaCommand(
                "update",
                "обновить элемент по id",
                "update id {element}",
                "update 1",
                "id передаётся в той же строке; далее вводятся поля Ticket.",
                (request, context) -> {
                    List<String> tokens = request.argumentTokens();
                    if (tokens.size() != 1) {
                        return CommandResult.error("Синтаксис: update id {element}");
                    }
                    Long id = parseLong(tokens.get(0), "id");
                    if (id == null) {
                        return CommandResult.error("id должен быть целым числом > 0.");
                    }
                    Optional<Ticket> existing = context.collectionService().findById(id);
                    if (existing.isEmpty()) {
                        return CommandResult.error("Элемент с id=" + id + " не найден.");
                    }

                    ObjectReadResult<TicketDraft> read = context.ticketFormReader().readTicketDraft();
                    if (read.isCanceled()) {
                        return CommandResult.error("Обновление отменено.");
                    }
                    if (read.error().isPresent()) {
                        return CommandResult.error(read.error().get());
                    }
                    Ticket updated = context.ticketFactory().createForUpdate(existing.get(), read.value().orElseThrow());
                    boolean ok = context.collectionService().updateById(id, updated);
                    if (!ok) {
                        return CommandResult.error("Не удалось обновить элемент с id=" + id + ".");
                    }
                    return CommandResult.success("Элемент с id=" + id + " обновлён.");
                }
        );
    }

    private Command removeById() {
        return new LambdaCommand(
                "remove_by_id",
                "удалить элемент по id",
                "remove_by_id id",
                "remove_by_id 3",
                "id должен быть целым числом > 0.",
                (request, context) -> {
                    List<String> tokens = request.argumentTokens();
                    if (tokens.size() != 1) {
                        return CommandResult.error("Синтаксис: remove_by_id id");
                    }
                    Long id = parseLong(tokens.get(0), "id");
                    if (id == null) {
                        return CommandResult.error("id должен быть целым числом > 0.");
                    }
                    boolean removed = context.collectionService().removeById(id);
                    if (!removed) {
                        return CommandResult.error("Элемент с id=" + id + " не найден.");
                    }
                    return CommandResult.success("Элемент с id=" + id + " удалён.");
                }
        );
    }

    private Command clear() {
        return new LambdaCommand(
                "clear",
                "очистить коллекцию",
                "clear",
                "clear",
                "Аргументы отсутствуют.",
                (request, context) -> {
                    if (hasArgs(request)) {
                        return CommandResult.error("Команда clear не принимает аргументы.");
                    }
                    context.collectionService().clear();
                    return CommandResult.success("Коллекция очищена.");
                }
        );
    }

    private Command save() {
        return new LambdaCommand(
                "save",
                "сохранить коллекцию в файл",
                "save",
                "save",
                "Аргументы отсутствуют.",
                (request, context) -> {
                    if (hasArgs(request)) {
                        return CommandResult.error("Команда save не принимает аргументы.");
                    }
                    SaveReport report = context.csvStorage().save(context.dataFile(), context.collectionService().snapshot());
                    if (report.isSuccess()) {
                        context.logger().logUser("Сохранение: " + report.getMessage());
                        return CommandResult.success(report.getMessage());
                    }
                    return CommandResult.error(report.getMessage());
                }
        );
    }

    private Command executeScript() {
        return new LambdaCommand(
                "execute_script",
                "исполнить команды из файла скрипта",
                "execute_script file_name",
                "execute_script script.txt",
                "Поддерживается стек скриптов; рекурсивное включение запрещено.",
                (request, context) -> {
                    if (request.getArgumentLine().isBlank()) {
                        return CommandResult.error("Синтаксис: execute_script file_name");
                    }
                    Path resolved = context.inputManager().resolveScriptPath(request.getArgumentLine());
                    try {
                        context.inputManager().pushScript(resolved);
                    } catch (IllegalStateException exception) {
                        return CommandResult.error(exception.getMessage());
                    } catch (IOException exception) {
                        return CommandResult.error("Не удалось открыть скрипт: " + exception.getMessage());
                    }
                    return CommandResult.success("Скрипт добавлен в стек: " + resolved.toAbsolutePath());
                }
        );
    }

    private Command exit() {
        return new LambdaCommand(
                "exit",
                "завершить программу без сохранения",
                "exit",
                "exit",
                "Аргументы отсутствуют.",
                (request, context) -> {
                    if (hasArgs(request)) {
                        return CommandResult.error("Команда exit не принимает аргументы.");
                    }
                    return CommandResult.exit("Завершение программы без сохранения.");
                }
        );
    }

    private Command addIfMax() {
        return new LambdaCommand(
                "add_if_max",
                "добавить элемент, если он больше максимального",
                "add_if_max {element}",
                "add_if_max",
                "После имени команды введите поля Ticket.",
                (request, context) -> {
                    if (hasArgs(request)) {
                        return CommandResult.error("Команда add_if_max не принимает аргументы в строке.");
                    }
                    ObjectReadResult<TicketDraft> read = context.ticketFormReader().readTicketDraft();
                    if (read.isCanceled()) {
                        return CommandResult.error("Операция отменена.");
                    }
                    if (read.error().isPresent()) {
                        return CommandResult.error(read.error().get());
                    }
                    TicketDraft draft = read.value().orElseThrow();
                    Ticket reference = context.ticketFactory().createTransient(draft);
                    Optional<Ticket> max = context.collectionService().max();
                    if (max.isPresent() && reference.compareTo(max.get()) <= 0) {
                        return CommandResult.success("Элемент не добавлен: он не превышает максимальный.");
                    }
                    Ticket ticket = context.ticketFactory().createNew(draft);
                    boolean added = context.collectionService().add(ticket);
                    if (!added) {
                        return CommandResult.error("Не удалось добавить элемент.");
                    }
                    return CommandResult.success("Элемент добавлен как новый максимум. id=" + ticket.getId());
                }
        );
    }

    private Command removeGreater() {
        return new LambdaCommand(
                "remove_greater",
                "удалить элементы, превышающие заданный",
                "remove_greater {element}",
                "remove_greater",
                "После имени команды введите поля Ticket для сравнения.",
                (request, context) -> {
                    if (hasArgs(request)) {
                        return CommandResult.error("Команда remove_greater не принимает аргументы в строке.");
                    }
                    ObjectReadResult<TicketDraft> read = context.ticketFormReader().readTicketDraft();
                    if (read.isCanceled()) {
                        return CommandResult.error("Операция отменена.");
                    }
                    if (read.error().isPresent()) {
                        return CommandResult.error(read.error().get());
                    }
                    Ticket reference = context.ticketFactory().createTransient(read.value().orElseThrow());
                    int removed = context.collectionService().removeGreater(reference);
                    return CommandResult.success("Удалено элементов: " + removed + ".");
                }
        );
    }

    private Command removeLower() {
        return new LambdaCommand(
                "remove_lower",
                "удалить элементы, меньшие заданного",
                "remove_lower {element}",
                "remove_lower",
                "После имени команды введите поля Ticket для сравнения.",
                (request, context) -> {
                    if (hasArgs(request)) {
                        return CommandResult.error("Команда remove_lower не принимает аргументы в строке.");
                    }
                    ObjectReadResult<TicketDraft> read = context.ticketFormReader().readTicketDraft();
                    if (read.isCanceled()) {
                        return CommandResult.error("Операция отменена.");
                    }
                    if (read.error().isPresent()) {
                        return CommandResult.error(read.error().get());
                    }
                    Ticket reference = context.ticketFactory().createTransient(read.value().orElseThrow());
                    int removed = context.collectionService().removeLower(reference);
                    return CommandResult.success("Удалено элементов: " + removed + ".");
                }
        );
    }

    private Command groupCountingByCreationDate() {
        return new LambdaCommand(
                "group_counting_by_creation_date",
                "сгруппировать элементы по creationDate",
                "group_counting_by_creation_date",
                "group_counting_by_creation_date",
                "Аргументы отсутствуют.",
                (request, context) -> {
                    if (hasArgs(request)) {
                        return CommandResult.error("Команда group_counting_by_creation_date не принимает аргументы.");
                    }
                    Map<ZonedDateTime, Long> grouped = context.collectionService().countByCreationDate();
                    if (grouped.isEmpty()) {
                        return CommandResult.success("Коллекция пуста.");
                    }
                    String message = grouped.entrySet().stream()
                            .map(entry -> entry.getKey() + " -> " + entry.getValue())
                            .collect(Collectors.joining(System.lineSeparator()));
                    return CommandResult.success(message, grouped);
                }
        );
    }

    private Command filterContainsName() {
        return new LambdaCommand(
                "filter_contains_name",
                "вывести элементы, name которых содержит подстроку",
                "filter_contains_name name",
                "filter_contains_name vip",
                "Подстрока передаётся в той же строке.",
                (request, context) -> {
                    if (request.getArgumentLine().isBlank()) {
                        return CommandResult.error("Синтаксис: filter_contains_name name");
                    }
                    LinkedHashSet<Ticket> filtered = context.collectionService().filterContainsName(request.getArgumentLine());
                    if (filtered.isEmpty()) {
                        return CommandResult.success("Подходящих элементов не найдено.");
                    }
                    String message = filtered.stream()
                            .map(Ticket::toString)
                            .collect(Collectors.joining(System.lineSeparator()));
                    return CommandResult.success(message, filtered);
                }
        );
    }

    private Command filterLessThanRefundable() {
        return new LambdaCommand(
                "filter_less_than_refundable",
                "вывести элементы с refundable меньше заданного",
                "filter_less_than_refundable refundable",
                "filter_less_than_refundable true",
                "Значение: true/false (или 1/0, yes/no).",
                (request, context) -> {
                    if (request.getArgumentLine().isBlank()) {
                        return CommandResult.error("Синтаксис: filter_less_than_refundable refundable");
                    }
                    Optional<Boolean> value = context.ticketFormReader().parseBoolean(request.getArgumentLine());
                    if (value.isEmpty()) {
                        return CommandResult.error("Некорректный аргумент. Допустимо: true/false, 1/0, yes/no.");
                    }
                    LinkedHashSet<Ticket> filtered = context.collectionService().filterLessThanRefundable(value.get());
                    if (filtered.isEmpty()) {
                        return CommandResult.success("Подходящих элементов не найдено.");
                    }
                    String message = filtered.stream()
                            .map(Ticket::toString)
                            .collect(Collectors.joining(System.lineSeparator()));
                    return CommandResult.success(message, filtered);
                }
        );
    }

    private Command history() {
        return new LambdaCommand(
                "history",
                "показать последние команды",
                "history",
                "history",
                "Аргументы отсутствуют.",
                (request, context) -> {
                    if (hasArgs(request)) {
                        return CommandResult.error("Команда history не принимает аргументы.");
                    }
                    List<String> list = context.history().list();
                    if (list.isEmpty()) {
                        return CommandResult.success("История пуста.");
                    }
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < list.size(); i += 1) {
                        builder.append(i + 1).append(". ").append(list.get(i)).append(System.lineSeparator());
                    }
                    return CommandResult.success(builder.toString().trim(), list);
                }
        );
    }

    private boolean hasArgs(final CommandRequest request) {
        return !request.getArgumentLine().isBlank();
    }

    private Long parseLong(final String raw, final String name) {
        try {
            long value = Long.parseLong(raw);
            if (value <= 0) {
                return null;
            }
            return value;
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
