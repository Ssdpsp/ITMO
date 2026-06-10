package lab5.client;

/**
 * Возникает, когда все UDP-попытки закончились без ответа.
 */
public class ServerUnavailableException extends Exception {
    public ServerUnavailableException(String message) {
        super(message);
    }
}
