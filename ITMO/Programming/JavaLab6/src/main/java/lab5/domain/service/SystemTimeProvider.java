package lab5.domain.service;

import java.time.ZonedDateTime;

/**
 * Использует системные часы
 */
public class SystemTimeProvider implements TimeProvider {
    @Override
    public ZonedDateTime now() {
        return ZonedDateTime.now();
    }
}
