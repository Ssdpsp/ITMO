package Org.infrastructure.storage;
/**
 * Результат сохранения collection.
 */
public final class SaveReport {
    private final boolean success;
    private final String message;

    /**
     * Создание отчёта.
     *
     * @param success success flag
     * @param message message
     */
    public SaveReport(final boolean success, final String message) {
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
