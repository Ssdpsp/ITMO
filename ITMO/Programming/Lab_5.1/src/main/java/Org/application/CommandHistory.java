package Org.application;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * История команд с ограничением в памяти.
 */
public final class CommandHistory {
    private final int maxSize;
    private final Deque<String> commands;

    /**
     * Создание буфера истории.
     *
     * @param maxSize max stored command count
     */
    public CommandHistory(final int maxSize) {
        this.maxSize = maxSize;
        this.commands = new ArrayDeque<>();
    }

    /**
     * Добавление команд в историю.
     *
     * @param command command name
     */
    public void add(final String command) {
        if (command == null || command.isBlank()) {
            return;
        }
        if (commands.size() >= maxSize) {
            commands.removeFirst();
        }
        commands.addLast(command);
    }

    /**
     * @return history snapshot
     */
    public List<String> list() {
        return new ArrayList<>(commands);
    }
}