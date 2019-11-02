package br.ufsc.ine5410.floripaland.movement;

import javax.annotation.Nonnull;
import java.util.Objects;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Point {
    private final double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public @Nonnull Point plusX(double x) {
        return new Point(getX()+x, getY());
    }
    public @Nonnull Point plusY(double y) {
        return new Point(getX(), getY()+y);
    }
    public @Nonnull Point plus(@Nonnull Point p) {
        return new Point(getX() + p.getX(), getY() + p.getY());
    }

    public double distanceTo(@Nonnull Point to) {
        return sqrt(pow(to.getX() - this.getX(), 2) + pow(to.getY() - this.getY(), 2));
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", getX(), getY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.getX(), getX()) == 0 &&
                Double.compare(point.getY(), getY()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }
}
