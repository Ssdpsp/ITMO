package Org.infrastructure.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Источник ввода на основе консоли/стандартного ввода данных.
 */
public final class ConsoleInputSource implements InputSource {
    private final BufferedReader reader;
    private final InputMode mode;

    /**
     * Создание источника стандартного ввда данных.
     *
     * @param inputStream source input stream
     */
    public ConsoleInputSource(final InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        this.mode = System.console() == null ? InputMode.REDIRECTED : InputMode.INTERACTIVE;
    }

    @Override
    public String readLine() throws IOException {
        return reader.readLine();
    }

    @Override
    public InputMode mode() {
        return mode;
    }

    @Override
    public String name() {
        return mode == InputMode.INTERACTIVE ? "console" : "redirected-stdin";
    }

    @Override
    public void close() {
        // stdin is managed by JVM; no-op.
    }
}

