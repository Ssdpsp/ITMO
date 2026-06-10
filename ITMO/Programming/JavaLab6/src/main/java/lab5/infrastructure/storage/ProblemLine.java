package lab5.infrastructure.storage;

/**
 * Запись отчета о поврежденной или невалидной строке CSV.
 *
 * @param lineNumber номер строки
 * @param rawLine исходное содержимое
 * @param reason причина
 */
public record ProblemLine(int lineNumber, String rawLine, String reason) {
}
