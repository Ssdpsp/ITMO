package lab5.application;

import java.util.List;

/**
 * Создает набор команд.
 */
public interface CommandFactory {
    /**
     * @return список команд
     */
    List<Command> createCommands();
}
