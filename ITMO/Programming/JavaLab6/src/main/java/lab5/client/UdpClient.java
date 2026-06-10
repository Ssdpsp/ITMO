package lab5.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import lab5.shared.CommandPacket;
import lab5.shared.NetworkResponse;
import lab5.shared.SerializationUtils;

/**
 * UDP-клиент, построенный на датаграммах.
 */
public class UdpClient implements Closeable {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private int retryCount;

    public UdpClient(
        String host,
        int port,
        int retryCount,
        int timeoutMillis
    ) throws IOException {
        this.socket = new DatagramSocket(); //создаю UDP сокет
        this.socket.setSoTimeout(timeoutMillis); // таймаут для выведения ошибки, если сервер не ответит
        this.address = InetAddress.getByName(host);
        this.port = port;
        this.retryCount = retryCount;
    }

    public NetworkResponse send(CommandPacket packet) throws IOException, ServerUnavailableException {
        byte[] data = SerializationUtils.serialize(packet); //процесс сериализации объекта CommandPacket в массив байтов
        if (data.length > SerializationUtils.MAX_DATAGRAM_SIZE) {
            throw new IOException("Команда слишком большая для одного UDP-пакета.");
        }

        DatagramPacket datagram = new DatagramPacket(data, data.length, address, port); //создание датаграммы: данные, адрес и порт получателя
        byte[] receiveBuffer = new byte[SerializationUtils.MAX_DATAGRAM_SIZE];

        for (int attempt = 1; attempt <= retryCount; attempt += 1) {
            socket.send(datagram); // отправка данных и ожидание ответа
            try {
                DatagramPacket responsePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(responsePacket);
                byte[] responseBytes = new byte[responsePacket.getLength()];
                System.arraycopy(responsePacket.getData(), responsePacket.getOffset(), responseBytes, 0, responsePacket.getLength());
                Object answer = SerializationUtils.deserialize(responseBytes);
                if (answer instanceof NetworkResponse response && response.getRequestId() == packet.getRequestId()) {
                    return response;
                }
            } catch (SocketTimeoutException exception) {
                System.err.println("Сервер не ответил, попытка " + attempt + " из " + retryCount + ".");
            } catch (ClassNotFoundException exception) {
                throw new IOException("Ответ сервера не удалось прочитать: " + exception.getMessage(), exception);
            }
        }

        throw new ServerUnavailableException("Сервер временно недоступен. Попробуйте команду позже.");
    }

    @Override
    public void close() {
        socket.close();
    }
}
