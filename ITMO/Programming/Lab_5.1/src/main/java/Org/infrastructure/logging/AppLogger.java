package Org.infrastructure.logging;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple two-channel logger: user-visible and technical diagnostics.
 */
public final class AppLogger implements Closeable {
    private final BufferedWriter userWriter;
    private final BufferedWriter technicalWriter;
    private final DateTimeFormatter formatter;

    /**
     * Создание логгера и файловых каналов.
     *
     * @param logsDir logs directory
     * @throws IOException if log files cannot be opened
     */
    public AppLogger(final Path logsDir) throws IOException {
        Files.createDirectories(logsDir);
        this.userWriter = Files.newBufferedWriter(
                logsDir.resolve("user.log"),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
        this.technicalWriter = Files.newBufferedWriter(
                logsDir.resolve("technical.log"),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
        this.formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    }

    /**
     * Запись действий пользователя.
     *
     * @param message message
     */
    public synchronized void logUser(final String message) {
        write(userWriter, "USER", message);
    }

    /**
     * Запись технических действий.
     *
     * @param message message
     */
    public synchronized void logTech(final String message) {
        write(technicalWriter, "TECH", message);
    }

    /**
     * Запись технической ошибки стека.
     *
     * @param message message
     * @param exception exception
     */
    public synchronized void logError(final String message, final Exception exception) {
        write(
                technicalWriter,
                "ERROR",
                message + " | " + exception.getClass().getSimpleName() + ": " + exception.getMessage()
        );
    }

    private void write(final BufferedWriter writer, final String level, final String message) {
        try {
            writer.write(formatter.format(ZonedDateTime.now()) + " [" + level + "] " + message);
            writer.newLine();
            writer.flush();
        } catch (IOException exception) {
            System.err.println("Не удалось записать лог: " + exception.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        IOException first = null;
        try {
            userWriter.close();
        } catch (IOException exception) {
            first = exception;
        }
        try {
            technicalWriter.close();
        } catch (IOException exception) {
            if (first == null) {
                first = exception;
            }
        }
        if (first != null) {
            throw first;
        }
    }
}

