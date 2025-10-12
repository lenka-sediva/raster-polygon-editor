package model.objectdata;

public class Point2D {
    public int x, y;

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point2D(double x, double y) {
        this.x = (int) Math.round(x);
        this.y = (int) Math.round(y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    // Euklidovská vzdálenost mezi tímto bodem a jiným bodem
    public double distanceTo(Point2D other) {
        if (other == null) return Double.MAX_VALUE;
        int dx = this.x - other.x; // (x1 - x2)
        int dy = this.y - other.y; // /(y1 - y2)
        return Math.sqrt(dx * dx + dy * dy); // √(dx² + dy²)
    }

    /**
     * Vzdálenost bodu p od úsečky definované body a a b
     * @param p - bod, jehož vzdálenost od úsečky počítáme
     * @param a - začátek úsečky
     * @param b - konec úsečky
     * @return nejkratší vzdálenost bodu p od úsečky ab
     */
    public static double distancePointToLine(Point2D p, Point2D a, Point2D b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double lengthSquared = dx * dx + dy * dy;

        if (lengthSquared == 0) return p.distanceTo(a);

        // parametr projekce bodu p na přímku ab
        double t = ((p.getX() - a.getX()) * dx + (p.getY() - a.getY()) * dy) / lengthSquared;
        t = Math.max(0, Math.min(1, t)); // omezíme t na <0,1>

        double projX = a.getX() + t * dx;
        double projY = a.getY() + t * dy;

        // vzdálenost mezi p a jeho projekcí
        return Math.sqrt(Math.pow(p.getX() - projX, 2) + Math.pow(p.getY() - projY, 2));
    }
}
