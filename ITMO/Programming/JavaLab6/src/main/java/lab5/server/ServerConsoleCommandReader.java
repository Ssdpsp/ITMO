package lab5.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Почти неблокирующий читатель консоли сервера. Он просто проверяет ready().
 */
public class ServerConsoleCommandReader {
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    public String readCommandIfReady() throws IOException {
        if (reader.ready()) {
            return reader.readLine();
        }
        return null;
    }
}
