package lab5.client;

import java.io.IOException;
import java.nio.file.Path;
import lab5.application.CommandResult;
import lab5.domain.validation.ValidationService;
import lab5.infrastructure.input.ConsoleInputSource;
import lab5.infrastructure.input.InputManager;
import lab5.infrastructure.input.ReadLine;
import lab5.infrastructure.logging.AppLogger;
import lab5.presentation.ConsolePresenter;
import lab5.presentation.TicketFormReader;
import lab5.shared.NetworkResponse;

/**
 * Точка входа клиента.
 */
public class ClientMain {
    private static String DEFAULT_HOST = "localhost";
    private static int DEFAULT_PORT = 5555;
    private static int RETRIES = 3;
    private static int TIMEOUT_MILLIS = 1500;

    private ClientMain() {
    }

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : DEFAULT_HOST;
        int port = args.length > 1 ? parsePort(args[1]) : DEFAULT_PORT;

        ConsolePresenter presenter = new ConsolePresenter(System.out, System.err);
        try (
            AppLogger logger = new AppLogger(Path.of("logs"));
            InputManager inputManager = new InputManager(new ConsoleInputSource(System.in), logger);
            UdpClient udpClient = new UdpClient(host, port, RETRIES, TIMEOUT_MILLIS)
        ) {
            ValidationService validationService = new ValidationService();
            TicketFormReader formReader = new TicketFormReader(inputManager, presenter, validationService);
            ClientCommandBuilder builder = new ClientCommandBuilder(inputManager, formReader, presenter);
            long requestCounter = 1L;

            presenter.println("Клиент запущен. Сервер: " + host + ":" + port);
            while (true) {
                presenter.printCommandPrompt(inputManager.currentMode());
                ReadLine line = inputManager.readLine();
                if (line == null) {
                    break;
                } ClientCommandBuilder.BuildResult buildResult = builder.build(line, requestCounter);
                if (buildResult.shouldExit()) {
                    break;
                }
                if (!buildResult.shouldSend()) {
                    continue;
                }

                requestCounter += 1L;
                try {        //обработка случая, когда клиент не может подключиться к серверу
                    NetworkResponse response = udpClient.send(buildResult.packet());
                    CommandResult result = response.getResult();
                    presenter.present(result);
                } catch (ServerUnavailableException exception) {
                    presenter.printError(exception.getMessage());
                } catch (IOException exception) {
                    presenter.printError("Ошибка обмена с сервером: " + exception.getMessage());
                }
            }
        } catch (IOException exception) {
            presenter.printError("Не удалось запустить клиент: " + exception.getMessage());
        }
    }

    private static int parsePort(String raw) {
        try {
            int parsed = Integer.parseInt(raw);
            if (parsed > 0 && parsed <= 65535) {
                return parsed;
            }
        } catch (NumberFormatException exception) {
            // below is the student-style fallback
        }
        System.err.println("Некорректный порт, используется " + DEFAULT_PORT + ".");
        return DEFAULT_PORT;
    }
}
