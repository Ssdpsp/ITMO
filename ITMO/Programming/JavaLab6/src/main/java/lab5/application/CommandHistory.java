package lab5.application;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Ограниченная история команд в памяти.
 */
public class CommandHistory {
    private int maxSize;
    private Deque<String> commands;

    /**
     * Создает буфер истории.
     *
     * @param maxSize максимальное количество хранимых команд
     */
    public CommandHistory(int maxSize) {
        this.maxSize = maxSize;
        this.commands = new ArrayDeque<>();
    }

    /**
     * Добавляет команду в историю.
     *
     * @param command имя команды
     */
    public void add(String command) {
        if (command == null || command.isBlank()) {
            return;
        }
        if (commands.size() >= maxSize) {
            commands.removeFirst();
        }
        commands.addLast(command);
    }

    /**
     * @return снимок истории
     */
    public List<String> list() {
        return new ArrayList<>(commands);
    }
}
