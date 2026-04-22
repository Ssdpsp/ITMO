package Org.domain.model;

import java.util.Objects;

/**
 * Координаты билета.
 */
public final class Coordinates {
    private final int x;
    private final Double y;

    /**
     * Создание координат.
     *
     * @param x x value
     * @param y y value
     */
    public Coordinates(final int x, final Double y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Coordinates{x=" + x + ", y=" + y + "}";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Coordinates that)) {
            return false;
        }
        return x == that.x && Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}