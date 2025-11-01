package model.objectdata;

import java.awt.*;

public class Line {
    private Point2D start;
    private Point2D end;
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

    /**
     * Zjistí, zda je úsečka vodorovná
     * @return true, pokud ano
     */
    public boolean isHorizontal() {
        return start.getY() == end.getY();
    }

    /**
     * Zajistí, aby úsečka směřovala shora dolů podle y souřadnice
     * Pokud start.y > end.y, start a end se prohodí
     */
    public void orientate() {
        if (start.getY() > end.getY()) {
            Point2D temp = start;
            start = end;
            end = temp;
        }
    }

    /**
     * Zjistí, zda existuje průsečík se skenovacím řádkem a hranou polygonu
     * @param y souřadnice skenovacího řádku
     * @return true, pokud průsečník existuje
     */
    public boolean intersectionExists(int y) {
        if (isHorizontal()) return false;
        return y >= start.getY() && y < end.getY();
    }

    /**
     * Vypočítá a vrátí x-ovou souřadnici průsečíku se skenovacím řádkem
     * @param y souřadnice skenovacího řádku
     * @return souřadnice x
     */
    public int getIntersection(int y) {
        if (!intersectionExists(y)) return -1;

        double dy = end.getY() - start.getY();
        double dx = end.getX() - start.getX();
        // určení relativní pozice řádku y mezi startem (0) a koncem (1) úsečky
        double ratio = (y - start.getY()) / dy;
        // interpolace x souřadnice průsečíku podle poměru
        return (int) Math.round(start.getX() + ratio * dx);
    }

    /**
     * Zjistí, zda je bod uvnitř polygonu
     * @param point, který chceme zkontrolovat
     * @param polygon, jehož vnitřek kontrolujeme
     * @return true, pokud je bod uvnitř polygonu
     */
    public static boolean isPointInsidePolygon(Point2D point, Polygon polygon) {
        int intersections = 0;
        int x = point.getX();
        int y = point.getY();

        for (int i = 0; i < polygon.size(); i++) {
            Point2D p1 = polygon.getItem(i);
            Point2D p2 = polygon.getItem((i + 1) % polygon.size());

            Line edge = new Line(p1, p2);
            edge.orientate();

            if(edge.intersectionExists(y)) {
                int xInt = edge.getIntersection(y);
                if (x < xInt) {
                    intersections++;
                }
            }
        }
        return (intersections % 2) != 0; // lichý počet průsečíků = uvnitř
    }
}
