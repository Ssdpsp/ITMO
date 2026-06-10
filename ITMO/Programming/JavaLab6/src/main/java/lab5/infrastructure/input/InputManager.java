package lab5.infrastructure.input;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import lab5.infrastructure.logging.AppLogger;

/**
 * Управляет стеком источников ввода и поддерживает вложенность скриптов.
 */
public class InputManager implements AutoCloseable {
    private Deque<SourceFrame> stack;
    private AppLogger logger;

    /**
     * Создает менеджер с корневым источником.
     *
     * @param root корневой источник ввода
     * @param logger логгер
     */
    public InputManager(InputSource root, AppLogger logger) {
        this.stack = new ArrayDeque<>();
        this.logger = logger;
        stack.push(new SourceFrame(root, null));
    }

    /**
     * Читает следующую строку из активного источника, автоматически убирая завершенные скрипты.
     *
     * @return следующая строка или null, если корневой источник закончился
     * @throws IOException при ошибках чтения
     */
    public ReadLine readLine() throws IOException {
        while (!stack.isEmpty()) {
            SourceFrame frame = stack.peek();
            String line = frame.source.readLine();
            if (line != null) {
                frame.lineNumber += 1;
                return new ReadLine(
                    line,
                    frame.source.name(),
                    frame.source.mode(),
                    frame.scriptPath,
                    frame.lineNumber
                );
            }
            if (stack.size() == 1) {
                return null;
            }
            stack.pop().source.close();
            logger.logTech("Скрипт завершён: " + frame.source.name());
        }
        return null;
    }

    /**
     * Добавляет источник-скрипт в стек.
     *
     * @param scriptPath путь к скрипту
     * @throws IOException при ошибках открытия или чтения
     */
    public void pushScript(Path scriptPath) throws IOException {
        Path normalized = scriptPath.toAbsolutePath().normalize();
        if (isScriptInStack(normalized)) {
            throw new IllegalStateException("Рекурсивное подключение скрипта запрещено: " + normalized);
        }
        ScriptInputSource source = new ScriptInputSource(normalized);
        stack.push(new SourceFrame(source, normalized));
        logger.logTech("Скрипт добавлен в стек: " + normalized);
    }

    /**
     * Разрешает путь скрипта относительно текущей директории скрипта, если она есть.
     *
     * @param raw исходный путь из команды
     * @return разрешенный нормализованный путь
     */
    public Path resolveScriptPath(String raw) {
        Path path = Path.of(raw);
        if (path.isAbsolute()) {
            return path.normalize();
        }
        Optional<Path> currentScriptDir = currentScriptDirectory();
        if (currentScriptDir.isPresent()) {
            return currentScriptDir.get().resolve(path).normalize();
        }
        return path.toAbsolutePath().normalize();
    }

    /**
     * @return текущий режим
     */
    public InputMode currentMode() {
        return stack.peek().source.mode();
    }

    /**
     * @return true, если текущий источник является скриптом
     */
    public boolean isScriptMode() {
        return currentMode() == InputMode.SCRIPT;
    }

    /**
     * @return путь к директории текущего обрабатываемого скрипта
     */
    public Optional<Path> currentScriptDirectory() {
        SourceFrame frame = stack.peek();
        if (frame.scriptPath == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(frame.scriptPath.getParent());
    }

    private boolean isScriptInStack(Path scriptPath) {
        for (SourceFrame frame : stack) {
            if (frame.scriptPath != null && Objects.equals(frame.scriptPath, scriptPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void close() throws IOException {
        IOException first = null;
        while (!stack.isEmpty()) {
            try {
                stack.pop().source.close();
            } catch (IOException exception) {
                if (first == null) {
                    first = exception;
                }
            }
        }
        if (first != null) {
            throw first;
        }
    }

    private static class SourceFrame {
        private InputSource source;
        private Path scriptPath;
        private int lineNumber;

        private SourceFrame(InputSource source, Path scriptPath) {
            this.source = source;
            this.scriptPath = scriptPath;
            this.lineNumber = 0;
        }
    }
}
