package lab5.server;

import java.net.SocketAddress;
import lab5.shared.CommandPacket;

/**
 * Датаграмма и разобранный запрос.
 */
public class ReceivedRequest {
    private SocketAddress address;
    private CommandPacket packet;
    private String readError;

    public ReceivedRequest(SocketAddress address, CommandPacket packet, String readError) {
        this.address = address;
        this.packet = packet;
        this.readError = readError;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public CommandPacket getPacket() {
        return packet;
    }

    public String getReadError() {
        return readError;
    }

    public boolean hasError() {
        return readError != null && !readError.isBlank();
    }
}
