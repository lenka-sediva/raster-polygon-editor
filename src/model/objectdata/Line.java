package model.objectdata;

import java.awt.*;

public class Line {
    private final Point2D start;
    private final Point2D end;
    private final int color;

    public Line(Point2D start, Point2D end) {
        this.start = start;
        this.end = end;
        this.color = Color.WHITE.getRGB();
    }

    public Line(Point2D start, Point2D end, int color) {
        this.start = start;
        this.end = end;
        this.color = color;
    }

    public Line(int x1, int y1, int x2, int y2) {
        this.start = new Point2D(x1, y1);
        this.end = new Point2D(x2, y2);
        this.color = Color.WHITE.getRGB();
    }

    public Line(int x1, int y1, int x2, int y2, int color) {
        this.start = new Point2D(x1, y1);
        this.end = new Point2D(x2, y2);
        this.color = color;
    }

    public Point2D getStart() {
        return start;
    }

    public Point2D getEnd() {
        return end;
    }

    public int getColor() {
        return color;
    }
}
