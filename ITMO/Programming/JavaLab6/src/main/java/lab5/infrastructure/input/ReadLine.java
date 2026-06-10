package lab5.infrastructure.input;

import java.nio.file.Path;

/**
 * Одна прочитанная строка ввода с метаданными.
 *
 * @param text содержимое строки
 * @param sourceName отображаемое имя источника
 * @param mode режим источника
 * @param sourcePath путь к файлу источника для скриптов, иначе null
 * @param lineNumber номер строки внутри источника, начиная с 1
 */
public record ReadLine(
    String text,
    String sourceName,
    InputMode mode,
    Path sourcePath,
    int lineNumber
) {
}
