package lab5.infrastructure.logging;

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
 * Простой двухканальный логгер: пользовательские сообщения и техническая диагностика.
 */
public class AppLogger implements Closeable {
    private BufferedWriter userWriter;
    private BufferedWriter technicalWriter;
    private DateTimeFormatter formatter;

    /**
     * Создает логгер и файловые каналы.
     *
     * @param logsDir директория логов
     * @throws IOException если файлы логов не удалось открыть
     */
    public AppLogger(Path logsDir) throws IOException {
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
     * Записывает пользовательское сообщение в лог.
     *
     * @param message сообщение
     */
    public void logUser(String message) {
        write(userWriter, "USER", message);
    }

    /**
     * Записывает техническое сообщение в лог.
     *
     * @param message сообщение
     */
    public void logTech(String message) {
        write(technicalWriter, "TECH", message);
    }

    /**
     * Записывает техническую ошибку с кратким описанием стека вызовов.
     *
     * @param message сообщение
     * @param exception исключение
     */
    public void logError(String message, Exception exception) {
        write(
            technicalWriter,
            "ERROR",
            message + " | " + exception.getClass().getSimpleName() + ": " + exception.getMessage()
        );
    }

    private void write(BufferedWriter writer, String level, String message) {
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
