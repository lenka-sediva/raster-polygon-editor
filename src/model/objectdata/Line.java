package model.objectdata;

import java.awt.*;

public class Line {
    private Point2D start;
    private Point2D end;
    private final int color;
    private Polygon polygon; // polygon, do kterého Line patří

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

    public Line(Point2D start, Point2D end, Polygon polygon) {
        this.start = start;
        this.end = end;
        this.color = Color.WHITE.getRGB();
        this.polygon = polygon;
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

    public Polygon getParentPolygon() {
        return polygon;
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
     * Vypočítá průsečík dvou úseček P a Q
     * @param p1 počáteční bod 1. úsečky
     * @param p2 koncový bod 1. úsečky
     * @param q1 počáteční bod 2. úsečky (hranice ořezávacího polygonu)
     * @param q2 koncový bod 2. úsečky
     * @return bod průsečíku
     */
    public static Point2D intersection(Point2D p1, Point2D p2, Point2D q1, Point2D q2) {
        double x1 = p1.getX(), y1 = p1.getY();
        double x2 = p2.getX(), y2 = p2.getY();
        double x3 = q1.getX(), y3 = q1.getY();
        double x4 = q2.getX(), y4 = q2.getY();

        double denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (denom == 0) throw new IllegalArgumentException("Rovnoběžné hrany – nelze spočítat průsečík");

        double px = ((x1*y2 - y1*x2)*(x3 - x4) - (x1 - x2)*(x3*y4 - y3*x4)) / denom;
        double py = ((x1*y2 - y1*x2)*(y3 - y4) - (y1 - y2)*(x3*y4 - y3*x4)) / denom;

        return new Point2D(px, py);
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

    public boolean isInside(Point2D p, int orientation) {
        // tečný vektor
        double tx = end.getX() - start.getX();
        double ty = end.getY() - start.getY();

        // normálový vektor
        double nx = -ty * orientation;
        double ny = tx * orientation;

        // směrový vektor k bodu p
        double sx = p.getX() - start.getX();
        double sy = p.getY() - start.getY();

        // skalární součin normálového a směrového vektoru
        double dot = nx * sx + ny * sy;

        // true: cos alpha musí být >= 0
        // bod je uvnitř nebo na hraně
        return dot >= 0;
    }
}
