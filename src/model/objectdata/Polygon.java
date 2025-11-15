package model.objectdata;

import java.util.ArrayList;

public class Polygon {

    private final ArrayList<Point2D> polygon;

    public Polygon() {
        this.polygon = new ArrayList<>();
    }

    public void addItem(Point2D p) {
        polygon.add(p);
    }

    public Point2D getItem(int index) {
        if (index >= 0 && index < polygon.size()) {
            return polygon.get(index);
        }
        return null;
    }

    public int size() {
        return polygon.size();
    }

    public void addItemToIndex(int index, Point2D p) {
        if (index >= 0 && index <= polygon.size()) {
            polygon.add(index, p);
        }
    }

    public void removeItem(int index) {
        if (index >= 0 && index < polygon.size()) {
            polygon.remove(index);
        }
    }

    public Point2D getFirst() {
        if (!polygon.isEmpty()) {
            return polygon.get(0);
        }
        return null;
    }

    public Point2D getLast() {
        if (!polygon.isEmpty()) {
            return polygon.get(polygon.size() -1);
        }
        return null;
    }

    public void clear() {
        polygon.clear();
    }

    public int findNearestPoint(Point2D cursor, Point2D[] points) {
        if (points == null || points.length == 0) {
            return -1;
        }

        if (polygon.isEmpty()) {
            return -1;
        }

        double minDistance = Double.MAX_VALUE;
        int nearestIndex = 0;

        for (int i = 0; i < points.length; i++) {
            double distance = cursor.distanceTo(points[i]);

            if(distance < minDistance) {
                minDistance = distance;
                nearestIndex = i;
            }
        }

        return nearestIndex;
    }

    // nalezení nejbližší hrany polygonu k bodu
    public static int findNearestLine(Point2D cursor, Polygon polygon) {
        if (polygon.size() < 2) return -1;

        double minDist = Double.MAX_VALUE;
        int nearestLineIndex = -1;

        for (int i = 0; i < polygon.size(); i++) {
            Point2D a = polygon.getItem(i);
            Point2D b = polygon.getItem((i + 1) % polygon.size());
            double dist = Point2D.distancePointToLine(cursor, a, b);
            if (dist < minDist) {
                minDist = dist;
                nearestLineIndex = i;
            }
        }
        return nearestLineIndex;
    }

    public Point2D findNearestPointToAxisCandidates(Line line) {
        Point2D start = line.getStart();
        Point2D cursor = line.getEnd();

        // proměnné pro přehlednější počítání s Point2D diagonal
        double dx = cursor.getX() - start.getX();
        double dy = cursor.getY() - start.getY();
        double min = Math.min(Math.abs(dx), Math.abs(dy));

        // možné cílové body: vodorovný, svislý, diagonální (45°)
        Point2D horizontal = new Point2D(cursor.getX(), start.getY());
        Point2D vertical = new Point2D(start.getX(), cursor.getY());
        Point2D diagonal = new Point2D(start.getX() + Math.signum(dx) * min, start.getY() + Math.signum(dy) * min);

        // nalezení nejbližšího z těchto bodů k pozici kurzoru
        Point2D[] candidates = { horizontal, vertical, diagonal };
        int nearestIndex = findNearestPoint(cursor, candidates);

        return candidates[nearestIndex];
    }

    // převede polygon na pole bodů
    public Point2D[] toArray() {
        Point2D[] arr = new Point2D[polygon.size()];
        for (int i = 0; i < polygon.size(); i++) {
            arr[i] = polygon.get(i);
        }
        return arr;
    }

    /**
     * Určí orientaci polygonu (CW nebo CCW)
     * @return +1 pokud je polygon CCW (proti směru hodinových ručiček)
     *         -1 pokud je polygon CW (ve směru hodinových ručiček)
     */
    public int getOrientation() {
        if (polygon.size() < 3) return 1; // výchozí CCW

        double sum = 0;

        for (int i = 0; i < polygon.size(); i++) {
            Point2D p1 = polygon.get(i);
            Point2D p2 = polygon.get((i + 1) % polygon.size());
            sum += (p2.getX() - p1.getX()) * (p2.getY() + p1.getY());
        }

        return sum < 0 ? 1 : -1; // CCW = 1, CW = -1
    }
}
