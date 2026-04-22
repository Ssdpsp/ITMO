package Org.infrastructure.storage;

/**
 * Неправильно сформированная/недопустимая запись отчета в виде строки CSV.
 *
 * @param lineNumber line number
 * @param rawLine raw content
 * @param reason reason
 */
public record ProblemLine(int lineNumber, String rawLine, String reason) {
}
