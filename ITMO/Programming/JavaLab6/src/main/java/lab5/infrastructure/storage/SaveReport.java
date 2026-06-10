package lab5.infrastructure.storage;

/**
 * Результат сохранения коллекции.
 */
public class SaveReport {
    private boolean success;
    private String message;

    /**
     * Создает отчет.
     *
     * @param success флаг успеха
     * @param message сообщение
     */
    public SaveReport(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
