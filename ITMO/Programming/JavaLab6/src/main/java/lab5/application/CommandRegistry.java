package lab5.application;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Реестр команд.
 */
public class CommandRegistry {
    private Map<String, Command> commands;

    /**
     * Создает пустой реестр.
     */
    public CommandRegistry() {
        this.commands = new LinkedHashMap<>();
    }

    /**
     * Регистрирует команду.
     *
     * @param command команда
     */
    public void register(Command command) {
        commands.put(command.name(), command);
    }

    /**
     * Ищет команду по имени.
     *
     * @param name имя
     * @return optional с командой
     */
    public Optional<Command> find(String name) {
        return Optional.ofNullable(commands.get(name));
    }

    /**
     * @return команды, отсортированные по имени
     */
    public Collection<Command> all() {
        return commands.values().stream()
            .sorted(Comparator.comparing(Command::name))
            .toList();
    }
}
