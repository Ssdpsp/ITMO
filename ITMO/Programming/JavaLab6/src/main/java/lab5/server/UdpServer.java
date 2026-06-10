package lab5.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import lab5.application.CommandResult;
import lab5.application.CommandResultStatus;
import lab5.shared.NetworkResponse;

/**
 * Однопоточный неблокирующий цикл UDP-сервера.
 */
public class UdpServer {
    private UdpConnectionReceiver connectionReceiver;
    private RequestReader requestReader;
    private ServerCommandProcessor commandProcessor;
    private ResponseSender responseSender;
    private ServerConsoleCommandReader consoleReader;
    private Logger logger;

    public UdpServer(
        UdpConnectionReceiver connectionReceiver,
        RequestReader requestReader,
        ServerCommandProcessor commandProcessor,
        ResponseSender responseSender,
        ServerConsoleCommandReader consoleReader,
        Logger logger
    ) {
        this.connectionReceiver = connectionReceiver;
        this.requestReader = requestReader;
        this.commandProcessor = commandProcessor;
        this.responseSender = responseSender;
        this.consoleReader = consoleReader;
        this.logger = logger;
    }

    public void run(int port) throws IOException {
        try (
            Selector selector = Selector.open(); // позволяет ждать пакеты на нескольких каналах сразу
            DatagramChannel channel = DatagramChannel.open() // открываем юдп канал
        ) {
            channel.configureBlocking(false); // переклчаю на неблокирующий режим, если пакетов нет, то receive вернет null
            channel.bind(new InetSocketAddress(port)); // привязка к порту
            channel.register(selector, SelectionKey.OP_READ); // только читаю
            logger.info("Сервер запущен на UDP-порту " + port + ".");
            System.out.println("Сервер запущен. Команды сервера: save, exit");

            boolean running = true;
            while (running) {
                selector.select(300); //ожидание пакетов до 300мс
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); // обработка пакетов
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isReadable()) {
                        readAllAvailable(channel);
                    }
                }
                running = processConsole(); // проверка консоли сервера на команды exit/save
            }
        }
    }

    private void readAllAvailable(DatagramChannel channel) {
        try {
            UdpConnectionReceiver.RawDatagram datagram = connectionReceiver.receive(channel);
            while (datagram != null) {
                handleDatagram(channel, datagram);
                datagram = connectionReceiver.receive(channel);
            }
        } catch (IOException exception) {
            logger.log(Level.WARNING, "Ошибка приема UDP-пакета", exception);
        }
    }

    private void handleDatagram(DatagramChannel channel, UdpConnectionReceiver.RawDatagram datagram) {
        ReceivedRequest request = requestReader.read(datagram);
        NetworkResponse response;
        if (request.hasError()) {
            response = new NetworkResponse(-1L, CommandResult.error(request.getReadError()));
        } else {
            CommandResult result = commandProcessor.process(request.getPacket());
            response = new NetworkResponse(request.getPacket().getRequestId(), result);
        }

        try {
            responseSender.send(channel, request.getAddress(), response);
        } catch (IOException exception) {
            logger.log(Level.WARNING, "Не удалось отправить ответ клиенту " + request.getAddress(), exception);
        }
    }

    private boolean processConsole() {
        try {
            String line = consoleReader.readCommandIfReady();
            if (line == null || line.isBlank()) {
                return true;
            }
            String command = line.trim().toLowerCase(Locale.ROOT);
            if ("save".equals(command)) {
                CommandResult result = commandProcessor.saveFromServer();
                printServerResult(result);
                return true;
            }
            if ("exit".equals(command) || "stop".equals(command)) {
                logger.info("Получена команда завершения сервера из консоли.");
                return false;
            }
            System.err.println("Неизвестная серверная команда. Доступно: save, exit");
            return true;
        } catch (IOException exception) {
            logger.log(Level.WARNING, "Ошибка чтения консоли сервера", exception);
            return true;
        }
    }

    private void printServerResult(CommandResult result) {
        if (result.getStatus() == CommandResultStatus.ERROR) {
            System.err.println(result.getMessage());
        } else {
            System.out.println(result.getMessage());
        }
    }
}
