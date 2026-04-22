package Org.infrastructure.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Файл - источник входных данных скрипта.
 */
public final class ScriptInputSource implements InputSource {
    private final Path path;
    private final BufferedReader reader;

    /**
     * Создание источника.
     *
     * @param path script path
     * @throws IOException when open fails
     */
    public ScriptInputSource(final Path path) throws IOException {
        this.path = path;
        this.reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
    }

    @Override
    public String readLine() throws IOException {
        return reader.readLine();
    }

    @Override
    public InputMode mode() {
        return InputMode.SCRIPT;
    }

    @Override
    public String name() {
        return "script:" + path.toAbsolutePath();
    }

    /**
     * @return script path
     */
    public Path path() {
        return path;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
