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

    // Euiklidovská vzdálenost mezi tímto bodem a jiným bodem
    public double distanceTo(Point2D other) {
        int dx = this.x - other.x; // (x1 - x2)
        int dy = this.y - other.y; // /(y1 - y2)
        return Math.sqrt(dx * dx + dy * dy); // √(dx² + dy²)
    }
}
