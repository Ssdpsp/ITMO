package Org.application;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Реестр команд.
 */
public final class CommandRegistry {
    private final Map<String, Command> commands;

    /**
     * Создание пустого реестра.
     */
    public CommandRegistry() {
        this.commands = new LinkedHashMap<>();
    }

    /**
     * Команда регистров.
     *
     * @param command command
     */
    public void register(final Command command) {
        commands.put(command.name(), command);
    }

    /**
     * Нахождение команды по имени.
     *
     * @param name name
     * @return optional command
     */
    public Optional<Command> find(final String name) {
        return Optional.ofNullable(commands.get(name));
    }

    /**
     * @return commands sorted by name
     */
    public Collection<Command> all() {
        return commands.values().stream()
                .sorted(Comparator.comparing(Command::name))
                .toList();
    }
}

