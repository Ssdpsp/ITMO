package lab5.infrastructure.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Источник ввода скрипта из файла.
 */
public class ScriptInputSource implements InputSource {
    private Path path;
    private BufferedReader reader;

    /**
     * Создает источник.
     *
     * @param path путь к скрипту
     * @throws IOException если открыть источник не удалось
     */
    public ScriptInputSource(Path path) throws IOException {
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
     * @return путь к скрипту
     */
    public Path path() {
        return path;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
