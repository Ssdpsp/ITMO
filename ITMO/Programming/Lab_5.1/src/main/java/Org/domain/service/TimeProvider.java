package Org.domain.service;

import java.time.ZonedDateTime;

/**
 * Доступ в текущее время.
 */
public interface TimeProvider {
    /**
     * @return current zoned date-time
     */
    ZonedDateTime now();
}