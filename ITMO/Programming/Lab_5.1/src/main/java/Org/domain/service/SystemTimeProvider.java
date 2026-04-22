package Org.domain.service;

import java.time.ZonedDateTime;

/**
 * Использование системных часов.
 */
public final class SystemTimeProvider implements TimeProvider {
    @Override
    public ZonedDateTime now() {
        return ZonedDateTime.now();
    }
}