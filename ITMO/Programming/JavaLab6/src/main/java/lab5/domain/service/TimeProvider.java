package lab5.domain.service;

import java.time.ZonedDateTime;

/**
 * Абстрагирует доступ к текущему времени.
 */
public interface TimeProvider {
    /**
     * @return текущие дата и время с часовым поясом
     */
    ZonedDateTime now();
}
