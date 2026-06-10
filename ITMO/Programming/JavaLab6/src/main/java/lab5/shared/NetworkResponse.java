package lab5.shared;

import java.io.Serializable;
import lab5.application.CommandResult;

/**
 * Сериализуемый ответ сервера.
 */
public class NetworkResponse implements Serializable {
    private static long serialVersionUID = 1L;

    private long requestId;
    private CommandResult result;

    public NetworkResponse(long requestId, CommandResult result) {
        this.requestId = requestId;
        this.result = result;
    }

    public long getRequestId() {
        return requestId;
    }

    public CommandResult getResult() {
        return result;
    }
}
