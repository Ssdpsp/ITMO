package lab5.shared;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Небольшой помощник для Java-сериализации UDP-пакетов.
 */
public class SerializationUtils {
    public static int MAX_DATAGRAM_SIZE = 65507;

    private SerializationUtils() {
    }
    public static byte[] serialize(Object object) throws IOException {
        try (
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream output = new ObjectOutputStream(bytes)
        ) {
            output.writeObject(object);
            output.flush();
            return bytes.toByteArray();
        }
    }
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (
            ByteArrayInputStream bytes = new ByteArrayInputStream(data);
            ObjectInputStream input = new ObjectInputStream(bytes)
        ) {
            return input.readObject();
        }
    }
}
