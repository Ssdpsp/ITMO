package lab5.server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Logger;
import lab5.application.CommandResult;
import lab5.shared.NetworkResponse;
import lab5.shared.SerializationUtils;

/**
 * Сериализует ответы и отправляет их клиентам.
 */
public class ResponseSender {
    private Logger logger;

    public ResponseSender(Logger logger) {
        this.logger = logger;
    }

    public void send(DatagramChannel channel, SocketAddress address, NetworkResponse response) throws IOException {
        byte[] data = SerializationUtils.serialize(response);
        NetworkResponse answer = response;
        if (data.length > SerializationUtils.MAX_DATAGRAM_SIZE) {
            answer = new NetworkResponse(
                response.getRequestId(),
                CommandResult.error("Ответ слишком большой для одного UDP-пакета.")
            );
            data = SerializationUtils.serialize(answer);
        }
        ByteBuffer buffer = ByteBuffer.wrap(data);
        channel.send(buffer, address);
        logger.info("Ответ отправлен клиенту " + address + ", requestId=" + answer.getRequestId());
    }
}
