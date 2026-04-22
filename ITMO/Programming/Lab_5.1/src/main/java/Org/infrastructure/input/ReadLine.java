package Org.infrastructure.input;

import java.nio.file.Path;

/**
 * Строка с метаданными.
 *
 * @param text line content
 * @param sourceName source display name
 * @param mode source mode
 * @param sourcePath source file path for scripts, null otherwise
 * @param lineNumber line number inside source (1-based)
 */
public record ReadLine(
        String text,
        String sourceName,
        InputMode mode,
        Path sourcePath,
        int lineNumber
) {
}
