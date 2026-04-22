package Org.infrastructure.input;

import java.io.IOException;

/**
 * Источник строк текстового ввода.
 */
public interface InputSource extends AutoCloseable {
    /**
     * Чтение строки или возвращение значения null в EOF.
     *
     * @return line or null
     * @throws IOException on read error
     */
    String readLine() throws IOException;

    /**
     * @return source mode
     */
    InputMode mode();

    /**
     * @return source display name
     */
    String name();

    @Override
    void close() throws IOException;
}
