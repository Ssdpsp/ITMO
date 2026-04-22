package Org.infrastructure.input;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import Org.infrastructure.logging.AppLogger;

/**
 * Управление стеком исходных данных и поддержание вложенности скриптов.
 */
public final class InputManager implements AutoCloseable {
    private final Deque<SourceFrame> stack;
    private final AppLogger logger;

    /**
     * Создание менеджера с корневым источником.
     *
     * @param root root input source
     * @param logger logger
     */
    public InputManager(final InputSource root, final AppLogger logger) {
        this.stack = new ArrayDeque<>();
        this.logger = logger;
        stack.push(new SourceFrame(root, null));
    }

    /**
     * Считывание следующей строки из активного источника, автоматически выводя завершенные скрипты.
     *
     * @return next line or null when root source is exhausted
     * @throws IOException on read errors
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
     * Помещение исходного кода скрипта в стек.
     *
     * @param scriptPath script path
     * @throws IOException on open/read errors
     */
    public void pushScript(final Path scriptPath) throws IOException {
        Path normalized = scriptPath.toAbsolutePath().normalize();
        if (isScriptInStack(normalized)) {
            throw new IllegalStateException("Рекурсивное подключение скрипта запрещено: " + normalized);
        }
        ScriptInputSource source = new ScriptInputSource(normalized);
        stack.push(new SourceFrame(source, normalized));
        logger.logTech("Скрипт добавлен в стек: " + normalized);
    }

    /**
     * Определение пути к скрипту в соответствии с текущим каталогом скриптов, если он есть.
     *
     * @param raw raw path from command
     * @return resolved normalized path
     */
    public Path resolveScriptPath(final String raw) {
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
     * @return current mode
     */
    public InputMode currentMode() {
        return stack.peek().source.mode();
    }

    /**
     * @return true when current source is script
     */
    public boolean isScriptMode() {
        return currentMode() == InputMode.SCRIPT;
    }

    /**
     * @return path of currently processed script directory
     */
    public Optional<Path> currentScriptDirectory() {
        SourceFrame frame = stack.peek();
        if (frame.scriptPath == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(frame.scriptPath.getParent());
    }

    private boolean isScriptInStack(final Path scriptPath) {
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

    private static final class SourceFrame {
        private final InputSource source;
        private final Path scriptPath;
        private int lineNumber;

        private SourceFrame(final InputSource source, final Path scriptPath) {
            this.source = source;
            this.scriptPath = scriptPath;
            this.lineNumber = 0;
        }
    }
}
