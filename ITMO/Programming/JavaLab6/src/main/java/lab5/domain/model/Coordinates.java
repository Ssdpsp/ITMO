package lab5.domain.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Координаты билета
 */
public class Coordinates implements Serializable {
    private static long serialVersionUID = 1L;
    private int x;
    private Double y;

    /**
     * Создает координаты.
     *
     * @param x значение x
     * @param y значение y
     */
    public Coordinates(int x, Double y) {
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
    public boolean equals(Object o) {
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
