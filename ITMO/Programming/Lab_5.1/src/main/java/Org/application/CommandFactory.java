package Org.application;

import java.util.List;

/**
 * Создание списка команд.
 */
public interface CommandFactory {
    /**
     * @return list of commands
     */
    List<Command> createCommands();
}
