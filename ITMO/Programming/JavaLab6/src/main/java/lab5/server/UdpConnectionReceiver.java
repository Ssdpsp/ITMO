package lab5.server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Logger;
import lab5.shared.SerializationUtils;

/**
 * Принимает UDP-датаграммы. В UDP нет настоящего подключения, но этот класс играет роль модуля приема.
 */
public class UdpConnectionReceiver {
    private Logger logger;

    public UdpConnectionReceiver(Logger logger) {
        this.logger = logger;
    }

    public RawDatagram receive(DatagramChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(SerializationUtils.MAX_DATAGRAM_SIZE);
        SocketAddress address = channel.receive(buffer);
        if (address == null) {
            return null;
        }
        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        logger.info("Получен UDP-пакет от " + address + ", байт: " + data.length);
        return new RawDatagram(address, data);
    }

    public record RawDatagram(SocketAddress address, byte[] data) {
    }
}
