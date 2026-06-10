package lab5.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lab5.shared.CommandPacket;
import lab5.shared.SerializationUtils;

/**
 * Читает и десериализует объекты запросов клиента.
 */
public class RequestReader {
    private Logger logger;

    public RequestReader(Logger logger) {
        this.logger = logger;
    }

    public ReceivedRequest read(UdpConnectionReceiver.RawDatagram datagram) { try {
            Object object = SerializationUtils.deserialize(datagram.data());
            if (!(object instanceof CommandPacket packet)) {
                return new ReceivedRequest(datagram.address(), null, "В пакете был не объект команды.");
            }
            logger.info("Получен запрос: " + packet.getCommand() + ", requestId=" + packet.getRequestId());
            return new ReceivedRequest(datagram.address(), packet, null);
        } catch (IOException | ClassNotFoundException exception) {
            logger.log(Level.WARNING, "Не удалось прочитать запрос", exception);
            return new ReceivedRequest(datagram.address(), null, "Не удалось прочитать запрос: " + exception.getMessage());
        }
    }
}
