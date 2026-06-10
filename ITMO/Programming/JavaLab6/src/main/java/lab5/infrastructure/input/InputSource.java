package lab5.infrastructure.input;

import java.io.IOException;

/**
 * Источник текстовых строк ввода.
 */
public interface InputSource extends AutoCloseable {
    /**
     * Читает одну строку или возвращает null при EOF.
     *
     * @return строка или null
     * @throws IOException при ошибке чтения
     */
    String readLine() throws IOException;

    /**
     * @return режим источника
     */
    InputMode mode();

    /**
     * @return отображаемое имя источника
     */
    String name();

    @Override
    void close() throws IOException;
}
